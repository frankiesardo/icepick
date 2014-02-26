package icepick.processor;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class IcepickProcessorTest {

  @Test public void fieldsMustNotBePrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle private int thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .failsToCompile();
  }

  @Test public void fieldsMustNotBeStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle static int thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .failsToCompile();
  }

  @Test public void fieldsMustNotBeFinal() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle final int thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .failsToCompile();
  }

  @Test public void classesMustNotBePrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  private class Inner {",
        "    @Icicle final int thing;",
        "  }",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .failsToCompile();
  }

  @Test public void notEveryObjectCanBePutInsideBundle() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle Object thing;",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .failsToCompile();
  }

  @Test public void simple() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle int thing;",
        "}"
    ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test.Test$$Icicle", Joiner.on("\n") .join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Test target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = savedInstanceState.getInt(BASE_KEY + \"thing\");",
            "  }",
            "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putInt(BASE_KEY + \"thing\", target.thing);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource);
  }

  @Test public void withParent() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle float thing;",
        "}",
        "class TestOne extends Test {",
        "  @Icicle double anotherThing;",
        "}",
        "class TestTwo extends Test {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Test target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = savedInstanceState.getFloat(BASE_KEY + \"thing\");",
            "  }",
            "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putFloat(BASE_KEY + \"thing\", target.thing);",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test.TestOne$$Icicle",
        Joiner.on('\n') .join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class TestOne$$Icicle {",
            "  private static final String BASE_KEY = \"test.TestOne$$Icicle.\";",
            "  public static void restoreInstanceState(TestOne target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.anotherThing = savedInstanceState.getDouble(BASE_KEY + \"anotherThing\");",
            "    test.Test$$Icicle.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public static void saveInstanceState(TestOne target, Bundle outState) {",
            "    test.Test$$Icicle.saveInstanceState(target, outState);",
            "    outState.putDouble(BASE_KEY + \"anotherThing\", target.anotherThing);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void withTypedParent() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test<T> {",
        "  @Icicle java.lang.String thing;",
        "}",
        "class TestOne extends Test<String> {",
        "  @Icicle java.lang.Long anotherThing;",
        "}",
        "class TestTwo extends Test<Integer> {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Test target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = savedInstanceState.getString(BASE_KEY + \"thing\");",
            "  }",
            "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putString(BASE_KEY + \"thing\", target.thing);",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test.TestOne$$Icicle",
        Joiner.on('\n') .join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class TestOne$$Icicle {",
            "  private static final String BASE_KEY = \"test.TestOne$$Icicle.\";",
            "  public static void restoreInstanceState(TestOne target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.anotherThing = savedInstanceState.getLong(BASE_KEY + \"anotherThing\");",
            "    test.Test$$Icicle.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public static void saveInstanceState(TestOne target, Bundle outState) {",
            "    test.Test$$Icicle.saveInstanceState(target, outState);",
            "    outState.putLong(BASE_KEY + \"anotherThing\", target.anotherThing);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void innerClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  class Inner {",
        "    @Icicle char[] thing;",
        "  }",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test$Inner$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$Inner$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$Inner$$Icicle.\";",
            "  public static void restoreInstanceState(Test.Inner target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = savedInstanceState.getCharArray(BASE_KEY + \"thing\");",
            "  }",
            "  public static void saveInstanceState(Test.Inner target, Bundle outState) {",
            "    outState.putCharArray(BASE_KEY + \"thing\", target.thing);",
            "  }",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource);
  }

  @Test public void typesAreDowncastWhenNecessary() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;", "import icepick.Icicle;", "public class Test {",
        "  static class AL<E> extends java.util.ArrayList<E> {}",
        "  static class SA<E> extends android.util.SparseArray<E> {}",

        "  @Icicle android.text.SpannableString charSequence;",
        "  @Icicle android.text.SpannableString[] charSequenceArray;",
        "  @Icicle android.accounts.Account parcelable;",
        "  @Icicle android.accounts.Account[] parcelableArray;",
        "  @Icicle java.io.File serializable;",
        "  @Icicle AL<java.lang.Integer> integerArrayList;",
        "  @Icicle AL<java.lang.String> stringArrayList;",
        "  @Icicle AL<java.lang.CharSequence> charSequenceArrayList;",
        "  @Icicle AL<android.os.Parcelable> parcelableArrayList;",
        "  @Icicle SA<android.os.Parcelable> sparseParcelableArray;",

        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Test target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.charSequence = (android.text.SpannableString) savedInstanceState.getCharSequence(BASE_KEY + \"charSequence\");",
            "    target.charSequenceArray = (android.text.SpannableString[]) savedInstanceState.getCharSequenceArray(BASE_KEY + \"charSequenceArray\");",
            "    target.parcelable = (android.accounts.Account) savedInstanceState.getParcelable(BASE_KEY + \"parcelable\");",
            "    target.parcelableArray = (android.accounts.Account[]) savedInstanceState.getParcelableArray(BASE_KEY + \"parcelableArray\");",
            "    target.serializable = (java.io.File) savedInstanceState.getSerializable(BASE_KEY + \"serializable\");",
            "    target.integerArrayList = (test.Test.AL<java.lang.Integer>) savedInstanceState.getIntegerArrayList(BASE_KEY + \"integerArrayList\");",
            "    target.stringArrayList = (test.Test.AL<java.lang.String>) savedInstanceState.getStringArrayList(BASE_KEY + \"stringArrayList\");",
            "    target.charSequenceArrayList = (test.Test.AL<java.lang.CharSequence>) savedInstanceState.getCharSequenceArrayList(BASE_KEY + \"charSequenceArrayList\");",
            "    target.parcelableArrayList = (test.Test.AL<android.os.Parcelable>) savedInstanceState.getParcelableArrayList(BASE_KEY + \"parcelableArrayList\");",
            "    target.sparseParcelableArray = (test.Test.SA<android.os.Parcelable>) savedInstanceState.getSparseParcelableArray(BASE_KEY + \"sparseParcelableArray\");",
            "  }",
            "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putCharSequence(BASE_KEY + \"charSequence\", target.charSequence);",
            "    outState.putCharSequenceArray(BASE_KEY + \"charSequenceArray\", target.charSequenceArray);",
            "    outState.putParcelable(BASE_KEY + \"parcelable\", target.parcelable);",
            "    outState.putParcelableArray(BASE_KEY + \"parcelableArray\", target.parcelableArray);",
            "    outState.putSerializable(BASE_KEY + \"serializable\", target.serializable);",
            "    outState.putIntegerArrayList(BASE_KEY + \"integerArrayList\", target.integerArrayList);",
            "    outState.putStringArrayList(BASE_KEY + \"stringArrayList\", target.stringArrayList);",
            "    outState.putCharSequenceArrayList(BASE_KEY + \"charSequenceArrayList\", target.charSequenceArrayList);",
            "    outState.putParcelableArrayList(BASE_KEY + \"parcelableArrayList\", target.parcelableArrayList);",
            "    outState.putSparseParcelableArray(BASE_KEY + \"sparseParcelableArray\", target.sparseParcelableArray);",
            "  }",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource);
  }

  @Test public void incompleteSerializableChainCompilesButFailsAtRuntime() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle Object[] thing;",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n').join("package test;", "import android.os.Bundle;",
            "import android.os.Parcelable;", "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Test target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = (java.lang.Object[]) savedInstanceState.getSerializable(BASE_KEY + \"thing\");",
            "  }",
            "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putSerializable(BASE_KEY + \"thing\", target.thing);",
            "  }",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource);
  }

  @Test public void viewReturnsParcelable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import icepick.Icicle;",
        "import android.widget.LinearLayout;",
        "import android.content.Context;",
        "public class Test extends LinearLayout{",
        "  public Test(Context context) {",
        "    super(context);",
        "  }",
        "  @Icicle android.os.Bundle thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n') .join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static Parcelable restoreInstanceState(Test target, Parcelable state) {",
            "    Bundle savedInstanceState = (Bundle) state;",
            "    Parcelable superState = savedInstanceState.getParcelable(BASE_KEY + \"$$SUPER$$\");",
            "    target.thing = savedInstanceState.getBundle(BASE_KEY + \"thing\");",
            "    return superState;",
            "  }",
            "  public static Parcelable saveInstanceState(Test target, Parcelable state) {",
            "    Bundle outState = new Bundle();",
            "    Parcelable superState = state;",
            "    outState.putParcelable(BASE_KEY + \"$$SUPER$$\", superState);",
            "    outState.putBundle(BASE_KEY + \"thing\", target.thing);",
            "    return outState;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource);
  }

  @Test public void viewWithParent() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "import android.widget.LinearLayout;",
        "import android.content.Context;",
        "public class Test extends LinearLayout{",
        "  public Test(Context context) {",
        "    super(context);",
        "  }",
        "  @Icicle short thing;",
        "}",
        "class TestOne extends Test {",
        "  public TestOne(Context context) {",
        "    super(context);",
        "  }",
        "  @Icicle float[] anotherThing;",
        "}",
        "class TestTwo extends Test {",
        "  public TestTwo(Context context) {",
        "    super(context);",
        "  }",
        "}"
        ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static Parcelable restoreInstanceState(Test target, Parcelable state) {",
            "    Bundle savedInstanceState = (Bundle)state;",
            "    Parcelable superState = savedInstanceState.getParcelable(BASE_KEY + \"$$SUPER$$\");",
            "    target.thing = savedInstanceState.getShort(BASE_KEY + \"thing\");",
            "    return superState;",
            "  }",
            "  public static Parcelable saveInstanceState(Test target, Parcelable state) {",
            "    Bundle outState = new Bundle();",
            "    Parcelable superState = state;",
            "    outState.putParcelable(BASE_KEY + \"$$SUPER$$\", superState);",
            "    outState.putShort(BASE_KEY + \"thing\", target.thing);",
            "    return outState;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test.TestOne$$Icicle",
        Joiner.on('\n') .join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class TestOne$$Icicle {",
            "  private static final String BASE_KEY = \"test.TestOne$$Icicle.\";",
            "  public static Parcelable restoreInstanceState(TestOne target, Parcelable state) {",
            "    Bundle savedInstanceState = (Bundle)state;",
            "    Parcelable superState = savedInstanceState.getParcelable(BASE_KEY + \"$$SUPER$$\");",
            "    target.anotherThing = savedInstanceState.getFloatArray(BASE_KEY + \"anotherThing\");",
            "    return test.Test$$Icicle.restoreInstanceState(target, superState);",
            "  }",
            "  public static Parcelable saveInstanceState(TestOne target, Parcelable state) {",
            "    Bundle outState = new Bundle();",
            "    Parcelable superState = test.Test$$Icicle.saveInstanceState(target, state);",
            "    outState.putParcelable(BASE_KEY + \"$$SUPER$$\", superState);",
            "    outState.putFloatArray(BASE_KEY + \"anotherThing\", target.anotherThing);",
            "    return outState;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource1, expectedSource2);
  }

  private Iterable<? extends Processor> icepickProcessors() {
    return Arrays.asList(new IcepickProcessor());
  }
}
