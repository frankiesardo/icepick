(ns icepick.processor
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [stencil.core :as mustache])
  (:import (icepick State
                    Icepick)
           (javax.tools Diagnostic$Kind)
           (javax.lang.model.type TypeMirror
                                  TypeKind)
           (javax.lang.model.element Element
                                     TypeElement
                                     Modifier)
           (javax.lang.model.util Elements
                                  ElementFilter
                                  Types)))

(def ^:private ^:dynamic *env*)

(defn- error [elem msg]
  (.. *env* getMessager (printMessage Diagnostic$Kind/ERROR msg elem)))

(defn- file-object [file-name elem]
  (.. *env* getFiler (createSourceFile file-name (into-array Element [elem]))))

(defn- package-name [^TypeElement elem]
  (.. *env* getElementUtils (getPackageOf elem) getQualifiedName toString))

(defn- type-element [name]
  (.. *env* getElementUtils (getTypeElement name)))

(defn- wildcard-type [name]
  (.. *env* getTypeUtils (getWildcardType (.asType (type-element name)) nil)))

(defn- declared-type [elem generic-type]
  (.. *env* getTypeUtils (getDeclaredType elem (into-array TypeMirror [generic-type]))))

(defn- assignable? [^TypeMirror type1 ^TypeMirror type2]
  (.. *env* getTypeUtils (isAssignable type1 type2)))

(def ^:private state-annotation "icepick.State" #_(.getName State))

;; emit-class!

(def ^:private template
  "// Generated code from Icepick. Do not modify!
package {{package}};
import android.os.Bundle;
{{#view?}}
import android.os.Parcelable;
{{/view?}}
import icepick.Bundler;
import icepick.Injector.Helper;
import icepick.Injector.{{type}};

import java.util.Map;
import java.util.HashMap;

public class {{name}}<T extends {{target}}> extends {{parent}}<T> {

  private final static Map<String, Bundler<?>> BUNDLERS = new HashMap<String, Bundler<?>>();
  static {
    {{#fields}} {{#bundler}}
    BUNDLERS.put(\"{{name}}\", new {{bundler}}());
    {{/bundler}} {{/fields}}
  }

  private final static Helper H = new Helper(\"{{package}}.{{name}}.\", BUNDLERS);

  {{^view?}}
  @Override public void restore(T target, Bundle state) {
    if (state == null) return;
    {{#fields}}
    target.{{name}} = H.get{{method}}(state, \"{{name}}\");
    {{/fields}}
    super.restore(target, state);
  }

  @Override public void save(T target, Bundle state) {
    super.save(target, state);
    {{#fields}}
    H.put{{method}}(state, \"{{name}}\", target.{{name}});
    {{/fields}}
  }
  {{/view?}}
  {{#view?}}
  @Override public Parcelable restore(T target, Parcelable p) {
    Bundle state = (Bundle) p;
    {{#fields}}
    target.{{name}} = H.get{{method}}(state, \"{{name}}\");
    {{/fields}}
    return super.restore(target, H.getParent(state));
  }

  @Override public Parcelable save(T target, Parcelable p) {
    Bundle state = H.putParent(super.save(target, p));
    {{#fields}}
    H.put{{method}}(state, \"{{name}}\", target.{{name}});
    {{/fields}}
    return state;
  }
  {{/view?}}
}")

(defn- emit-class!
  [[class fields]]
  (let [vals {:view?   (:view? class)
              :type    (if (:view? class) "View" "Object")
              :package (:package class)
              :name    (str (:dollar-name class) Icepick/SUFFIX)
              :target  (:dotted-name class)
              :parent  (if-let [parent (:qualified-parent-name class)]
                         (str parent Icepick/SUFFIX)
                         (if (:view? class) "View" "Object"))
              :fields  fields}
        file-name (str (:package class) "." (:dollar-name class) Icepick/SUFFIX)
        file-object (file-object file-name (:element class))]
    (doto (.openWriter file-object)
      (.write (mustache/render-string template vals))
      (.flush)
      (.close))))

;; enclosing class

(def ^:private analyzed-classes
  (atom {:annotated     {}
         :not-annotated #{}}))

(defn- view? [elem]
  (assignable? (.asType elem) (.asType (type-element "android.view.View"))))

(defn- class-info [^TypeElement elem]
  (let [qualified-name (str (.getQualifiedName elem))
        package (package-name elem)
        dotted-name (subs qualified-name (inc (count package)))
        dollar-name (str/replace dotted-name #"\." "\\$")]
    {:package     package
     :dotted-name dotted-name
     :dollar-name dollar-name}))

(defn- annotated-class? [^TypeElement elem]
  (seq (for [field (ElementFilter/fieldsIn (.getEnclosedElements elem))
             ann (.getAnnotationMirrors field)
             :when (= state-annotation (-> ann .getAnnotationType .asElement str))]
         field)))

(defn- qualified-parent-name [^TypeElement elem]
  (loop [^TypeMirror type (.getSuperclass elem)]
    (when-not (= (.getKind type) TypeKind/NONE)
      (let [class-element (.asElement type)
            class-name (str class-element)]
        (when-not (or (.startsWith class-name Icepick/JAVA_PREFIX)
                      (.startsWith class-name Icepick/ANDROID_PREFIX))
          (cond
            (contains? (:annotated @analyzed-classes) class-name)
            (get-in @analyzed-classes [:annotated class-name])

            (contains? (:not-annotated @analyzed-classes) class-name)
            (recur (.getSuperclass class-element))

            (annotated-class? class-element)
            (let [{:keys [package dollar-name]} (class-info class-element)
                  qualified-name (str package "." dollar-name)]
              (swap! analyzed-classes assoc-in
                     [:annotated class-name] qualified-name)
              qualified-name)

            :else
            (do
              (swap! analyzed-classes update-in [:not-annotated] conj class-name)
              (recur (.getSuperclass class-element)))))))))

(defn- enclosing-class [^TypeElement elem]
  (when (some #{Modifier/PRIVATE} (.getModifiers elem))
    (error elem "Enclosing class must not be private"))
  (assoc (class-info elem)
    :elem elem
    :view? (view? elem)
    :qualified-parent-name (qualified-parent-name elem)))

;; bundle-method

(def ^:private exact-types
  {"boolean"                                     "Boolean",
   "boolean[]"                                   "BooleanArray",
   "java.lang.Boolean"                           "BoxedBoolean",
   "byte"                                        "Byte",
   "byte[]"                                      "ByteArray",
   "java.lang.Byte"                              "BoxedByte",
   "char"                                        "Char",
   "char[]"                                      "CharArray",
   "java.lang.Char"                              "BoxedChar",
   "double"                                      "Double",
   "double[]"                                    "DoubleArray",
   "java.lang.Double"                            "BoxedDouble",
   "float"                                       "Float",
   "float[]"                                     "FloatArray",
   "java.lang.Float"                             "BoxedFloat",
   "int"                                         "Int",
   "int[]"                                       "IntArray",
   "java.lang.Integer"                           "BoxedInt"
   "long"                                        "Long",
   "long[]"                                      "LongArray",
   "java.lang.Long"                              "BoxedLong",
   "short"                                       "Short",
   "short[]"                                     "ShortArray",
   "java.lang.Short"                             "BoxedShort",

   "java.lang.CharSequence"                      "CharSequence",
   "java.lang.CharSequence[]"                    "CharSequenceArray",
   "java.lang.String"                            "String",
   "java.lang.String[]"                          "StringArray",
   "java.util.ArrayList<java.lang.CharSequence>" "CharSequenceArrayList",
   "java.util.ArrayList<java.lang.Integer>"      "IntegerArrayList",
   "java.util.ArrayList<java.lang.String>"       "StringArrayList",

   "android.os.Bundle"                           "Bundle",
   "android.os.Parcelable[]"                     "ParcelableArray"})

(defn- parcelable? [type]
  (assignable? type (.asType (type-element "android.os.Parcelable"))))

(defn- serializable? [type]
  (assignable? type (.asType (type-element "java.io.Serializable"))))

(defn- parcelable-array-list? [type]
  (and (not= TypeKind/WILDCARD (.getKind type))
       (assignable? type (declared-type
                           (type-element "java.util.ArrayList")
                           (wildcard-type "android.os.Parcelable")))))

(defn- sparse-parcelable-array? [type]
  (and (not= TypeKind/WILDCARD (.getKind type))
       (assignable? type (declared-type
                           (type-element "android.util.SparseArray")
                           (wildcard-type "android.os.Parcelable")))))

(defn- bundle-method [^TypeMirror type]
  (or (get exact-types (str type))
      (and (parcelable? type) "Parcelable")
      (and (parcelable-array-list? type) "ParcelableArrayList")
      (and (sparse-parcelable-array? type) "SparseParcelableArray")
      (and (serializable? type) "Serializable")))

(defn- bundler [^Element elem]
  (->> (.getAnnotationMirrors elem)
       (filter #(= (.. % getAnnotationType toString) state-annotation))
       (mapcat #(.getElementValues %))
       (filter #(= (.. % getKey getSimpleName toString) "value"))
       (map #(.. % getValue getValue asElement toString))
       (first)))

(defn- analyze-field
  "Converts javax Element into a suitable representation for code generation."
  [^Element elem]
  (when (some #{Modifier/PRIVATE Modifier/STATIC Modifier/FINAL} (.getModifiers elem))
    (error elem "Field must not be private, static or final"))
  (let [type (.asType elem)
        bundler (bundler elem)
        bundle-method (bundle-method type)]
    (when-not (or bundler bundle-method)
      (error elem (str "Don't know how to put a " type " inside a Bundle")))
    (cond-> {:name            (.. elem getSimpleName toString)
             :enclosing-class (enclosing-class (.getEnclosingElement elem))}
            bundler (merge {:bundler bundler
                            :method  "WithBundler"})
            (not bundler) (merge {:bundler false
                                  :method  bundle-method}))))

(defn process
  [processing-env annotations env]
  (binding [*env* processing-env]
    (doseq [ann annotations]
      (->> (.getElementsAnnotatedWith env ann)
           (map analyze-field)
           (group-by :enclosing-class)
           (map emit-class!)
           (doall)))))
