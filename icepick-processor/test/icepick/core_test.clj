(ns icepick.core-test
  (:require [clojure.test :refer :all]
            [clojure.string :as string])
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

(defn- make-source [[file content]]
  (JavaFileObjects/forSourceString file (string/join "\n" content)))

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
    (check-fails
     ["test.Test"
      ["package test;"
       "import icepick.State;"
       "public class Test {"
       "  @State private int f1;"
       "}"]]))
  (testing "private class"
    (check-fails
     ["test.Test"
      ["package test;"
       "import icepick.State;"
       "public class Test {"
       "  private static class Inner {"
       "    @State private int f1;"
       "  }"
       "}"]])))

(deftest simple
  (check-compiles
   ["test.Test"
    ["package test;"
     "import icepick.State;"
     "public class Test {"
     "  @State int f1;"
     "  @State boolean f2;"
     "  @State char[] f3;"
     "}"]]
   ["test.Test$$Icepick"
    ["package test;"
     "import android.os.Bundle;"
     "import icepick.Injector.Helper;"
     "import icepick.Injector.Object;"
     "public class Test$$Icepick<T extends Test> extends Object<T> {"
     "  private final static Helper H = new Helper(\"test.Test$$Icepick.\");"
     "  public void restore(T target, Bundle state) {"
     "    if (state == null) return;"
     "    target.f1 = H.getInt(state, \"f1\");"
     "    target.f2 = H.getBoolean(state, \"f2\");"
     "    target.f3 = H.getCharArray(state, \"f3\");"
     "    super.restore(target, state);"
     "  }"
     "  public void save(T target, Bundle state) {"
     "    super.save(target, state);"
     "    H.putInt(state, \"f1\", target.f1);"
     "    H.putBoolean(state, \"f2\", target.f2);"
     "    H.putCharArray(state, \"f3\", target.f3);"
     "  }"
     "}"]]))

(deftest boxed
  (check-compiles
   ["test.Test"
    ["package test;"
     "import icepick.State;"
     "public class Test {"
     "  @State Float f1;"
     "}"]]
   ["test.Test$$Icepick"
    ["package test;"
     "import android.os.Bundle;"
     "import icepick.Injector.Helper;"
     "import icepick.Injector.Object;"
     "public class Test$$Icepick<T extends Test> extends Object<T> {"
     "  private final static Helper H = new Helper(\"test.Test$$Icepick.\");"
     "  public void restore(T target, Bundle state) {"
     "    if (state == null) return;"
     "    target.f1 = H.getBoxedFloat(state, \"f1\");"
     "    super.restore(target, state);"
     "  }"
     "  public void save(T target, Bundle state) {"
     "    super.save(target, state);"
     "    H.putBoxedFloat(state, \"f1\", target.f1);"
     "  }"
     "}"]]))

(deftest generics
  (testing "T loses type when restored"
    (check-fails
     ["test.Test"
      ["package test;"
       "import icepick.State;"
       "public class Test<T extends CharSequence> {"
       "  @State T f1;"
       "}"]]))
  (testing "unless T is Parcelable"
    (check-compiles
     ["test.Test"
      ["package test;"
       "import icepick.State;"
       "import android.os.Parcelable;"
       "public class Test<T extends Parcelable> {"
       "  @State T f1;"
       "}"]]
     ["test.Test$$Icepick"
      ["package test;"
       "import android.os.Bundle;"
       "import icepick.Injector.Helper;"
       "import icepick.Injector.Object;"
       "public class Test$$Icepick<T extends Test> extends Object<T> {"
       "  private final static Helper H = new Helper(\"test.Test$$Icepick.\");"
       "  public void restore(T target, Bundle state) {"
       "    if (state == null) return;"
       "    target.f1 = H.getParcelable(state, \"f1\");"
       "    super.restore(target, state);"
       "  }"
       "  public void save(T target, Bundle state) {"
       "    super.save(target, state);"
       "    H.putParcelable(state, \"f1\", target.f1);"
       "  }"
       "}"]]))
  (testing "collection type is inferred"
    (check-compiles
     ["test.Test"
      ["package test;"
       "import icepick.State;"
       "import java.util.ArrayList;"
       "import android.util.SparseArray;"
       "import android.os.Parcelable;"
       "public class Test<T extends Parcelable> {"
       "  @State ArrayList<T> f1;"
       "  @State SparseArray<T> f2;"
       "}"]]
     ["test.Test$$Icepick"
      ["package test;"
       "import android.os.Bundle;"
       "import icepick.Injector.Helper;"
       "import icepick.Injector.Object;"
       "public class Test$$Icepick<T extends Test> extends Object<T> {"
       "  private final static Helper H = new Helper(\"test.Test$$Icepick.\");"
       "  public void restore(T target, Bundle state) {"
       "    if (state == null) return;"
       "    target.f1 = H.getParcelableArrayList(state, \"f1\");"
       "    target.f2 = H.getSparseParcelableArray(state, \"f2\");"
       "    super.restore(target, state);"
       "  }"
       "  public void save(T target, Bundle state) {"
       "    super.save(target, state);"
       "    H.putParcelableArrayList(state, \"f1\", target.f1);"
       "    H.putSparseParcelableArray(state, \"f2\", target.f2);"
       "  }"
       "}"]]))
  (testing "but extended collections and arrays are treated as Serializable"
    (check-compiles
     ["test.Test"
      ["package test;"
       "import icepick.State;"
       "import java.util.ArrayList;"
       "import android.os.Bundle;"
       "public class Test {"
       "  static class AL<T> extends ArrayList<T> {}"
       "  @State AL<Integer> f1;"
       "  @State AL<String> f2;"
       "  @State AL<CharSequence> f3;"
       "  @State Bundle[] f4;"
       "  @State StringBuffer[] f5;"
       "}"]]
     ["test.Test$$Icepick"
      ["package test;"
       "import android.os.Bundle;"
       "import icepick.Injector.Helper;"
       "import icepick.Injector.Object;"
       "public class Test$$Icepick<T extends Test> extends Object<T> {"
       "  private final static Helper H = new Helper(\"test.Test$$Icepick.\");"
       "  public void restore(T target, Bundle state) {"
       "    if (state == null) return;"
       "    target.f1 = H.getSerializable(state, \"f1\");"
       "    target.f2 = H.getSerializable(state, \"f2\");"
       "    target.f3 = H.getSerializable(state, \"f3\");"
       "    target.f4 = H.getSerializable(state, \"f4\");"
       "    target.f5 = H.getSerializable(state, \"f5\");"
       "    super.restore(target, state);"
       "  }"
       "  public void save(T target, Bundle state) {"
       "    super.save(target, state);"
       "    H.putSerializable(state, \"f1\", target.f1);"
       "    H.putSerializable(state, \"f2\", target.f2);"
       "    H.putSerializable(state, \"f3\", target.f3);"
       "    H.putSerializable(state, \"f4\", target.f4);"
       "    H.putSerializable(state, \"f5\", target.f5);"
       "  }"
       "}"]])))

(deftest with-parent
  (check-compiles
   ["test.Test"
    ["package test;"
     "import icepick.State;"
     "import android.os.Parcelable;"
     "import android.os.Bundle;"
     "public class Test<T extends Parcelable> {"
     "  @State T f1;"
     "  static class Inner extends Test<Bundle> {"
     "    @State String f2;"
     "  }"
     "}"]]
   ["test.Test$$Icepick"
    ["package test;"
     "import android.os.Bundle;"
     "import icepick.Injector.Helper;"
     "import icepick.Injector.Object;"
     "public class Test$$Icepick<T extends Test> extends Object<T> {"
     "  private final static Helper H = new Helper(\"test.Test$$Icepick.\");"
     "  public void restore(T target, Bundle state) {"
     "    if (state == null) return;"
     "    target.f1 = H.getParcelable(state, \"f1\");"
     "    super.restore(target, state);"
     "  }"
     "  public void save(T target, Bundle state) {"
     "    super.save(target, state);"
     "    H.putParcelable(state, \"f1\", target.f1);"
     "  }"
     "}"]]
   ["test.Test$Inner$$Icepick"
    ["package test;"
     "import android.os.Bundle;"
     "import icepick.Injector.Helper;"
     "import icepick.Injector.Object;"
     "public class Test$Inner$$Icepick<T extends Test.Inner> extends test.Test$$Icepick<T> {"
     "  private final static Helper H = new Helper(\"test.Test$Inner$$Icepick.\");"
     "  public void restore(T target, Bundle state) {"
     "    if (state == null) return;"
     "    target.f2 = H.getString(state, \"f2\");"
     "    super.restore(target, state);"
     "  }"
     "  public void save(T target, Bundle state) {"
     "    super.save(target, state);"
     "    H.putString(state, \"f2\", target.f2);"
     "  }"
     "}"]]))

(deftest views
  (check-compiles
   ["test.Test"
    ["package test;"
     "import icepick.State;"
     "import android.view.View;"
     "import android.content.Context;"
     "import android.os.Parcelable;"
     "import android.os.Bundle;"
     "public class Test<T extends Parcelable> extends View {"
     "  public Test(Context c) {super(c);}"
     "  @State T f1;"
     "  static class Inner extends Test<Bundle> {"
     "    public Inner(Context c) {super(c);}"
     "    @State String f2;"
     "  }"
     "}"]]
   ["test.Test$$Icepick"
    ["package test;"
     "import android.os.Bundle;"
     "import android.os.Parcelable;"
     "import icepick.Injector.Helper;"
     "import icepick.Injector.View;"
     "public class Test$$Icepick<T extends Test> extends View<T> {"
     "  private final static Helper H = new Helper(\"test.Test$$Icepick.\");"
     "  public Parcelable restore(T target, Parcelable p) {"
     "    Bundle state = (Bundle) p;"
     "    target.f1 = H.getParcelable(state, \"f1\");"
     "    return super.restore(target, H.getParent(state));"
     "  }"
     "  public Parcelable save(T target, Parcelable p) {"
     "    Bundle state = H.putParent(super.save(target, p));"
     "    H.putParcelable(state, \"f1\", target.f1);"
     "    return state;"
     "  }"
     "}"]]
   ["test.Test$Inner$$Icepick"
    ["package test;"
     "import android.os.Bundle;"
     "import android.os.Parcelable;"
     "import icepick.Injector.Helper;"
     "import icepick.Injector.View;"
     "public class Test$Inner$$Icepick<T extends Test.Inner> extends test.Test$$Icepick<T> {"
     "  private final static Helper H = new Helper(\"test.Test$Inner$$Icepick.\");"
     "  public Parcelable restore(T target, Parcelable p) {"
     "    Bundle state = (Bundle) p;"
     "    target.f2 = H.getString(state, \"f2\");"
     "    return super.restore(target, H.getParent(state));"
     "  }"
     "  public Parcelable save(T target, Parcelable p) {"
     "    Bundle state = H.putParent(super.save(target, p));"
     "    H.putString(state, \"f2\", target.f2);"
     "    return state;"
     "  }"
     "}"]]))
