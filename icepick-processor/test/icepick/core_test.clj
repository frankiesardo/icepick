(ns icepick.core-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.java.io :as io])
  (:use icepick.processor)
  (:import [icepick.processor IcepickProcessor]
           [javax.tools JavaFileObject]
           [com.google.testing.compile JavaFileObjects]
           [com.google.testing.compile JavaSourceSubjectFactory]
           [org.truth0 Truth]))

(defn- java-source []
  (JavaSourceSubjectFactory/javaSource))

(defn- icepick-processors []
  [(IcepickProcessor.)])

(defn- make-source [class]
  (let [file (str (str/replace class #"\." "/") ".java")
        content (slurp (io/resource file))]
    (JavaFileObjects/forSourceString class content)))

(defn- check-fails [input]
  (is (-> (Truth/ASSERT)
          (.about (java-source))
          (.that (make-source input))
          (.processedWith (icepick-processors))
          (.failsToCompile))))

(defn- check-compiles
  ([input]
   (let [input-source (make-source input)]
     (is (-> (Truth/ASSERT)
             (.about (java-source))
             (.that input-source)
             (.processedWith (icepick-processors))
             (.compilesWithoutError)))))
  ([input output & outputs]
   (let [[first & rest] (seq (map make-source (cons output outputs)))]
     (when-let [compiles (check-compiles input)]
       (-> compiles
           (.and)
           (.generatesSources first (into-array JavaFileObject rest)))))))

(deftest failures
  (testing "private field"
    (check-fails "test.failures.Test1"))
  (testing "private class"
    (check-fails "test.failures.Test2")))

(deftest simple
  (check-compiles "test.simple.Test"
                  "test.simple.Test$$Icepick"))

(deftest boxed
  (check-compiles "test.boxed.Test"
                  "test.boxed.Test$$Icepick"))

(deftest generics
  (testing "T loses type when restored"
    (check-fails "test.generics.Test1"))
  (testing "unless T is Parcelable"
    (check-compiles "test.generics.Test2"
                    "test.generics.Test2$$Icepick"))
  (testing "collection type is inferred"
    (check-compiles "test.generics.Test3"
                    "test.generics.Test3$$Icepick"))
  (testing "but extended collections and arrays are treated as Serializable"
    (check-compiles "test.generics.Test4"
                    "test.generics.Test4$$Icepick")))

(deftest with-parent
  (check-compiles "test.parent.Test"
                  "test.parent.Test$$Icepick"
                  "test.parent.Test$Inner$$Icepick"))

(deftest views
  (check-compiles "test.views.Test"
                  "test.views.Test$$Icepick"
                  "test.views.Test$Inner$$Icepick"))

(deftest custom-bundler
  (check-compiles "test.bundler.Test"
                  "test.bundler.Test$$Icepick"))
