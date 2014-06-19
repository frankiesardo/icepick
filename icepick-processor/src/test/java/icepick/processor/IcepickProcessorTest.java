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
        "}"));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test.Test$$Icicle", Joiner.on("\n") .join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "import icepick.StateHelper;",
            "public class Test$$Icicle implements StateHelper<Bundle> {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  private final StateHelper<Bundle> parent = (StateHelper<Bundle>) StateHelper.NO_OP;",
            "  public Bundle restoreInstanceState(Object obj, Bundle state) {",
            "    Test target = (Test) obj;",
            "    if (state == null) {",
            "      return null;",
            "    }",
            "    Bundle savedInstanceState = state;",
            "    target.thing = savedInstanceState.getInt(BASE_KEY + \"thing\");",
            "    return parent.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public Bundle saveInstanceState(Object obj, Bundle state) {",
            "    Test target = (Test) obj;",
            "    parent.saveInstanceState(target, state);",
            "    Bundle outState = state;",
            "    outState.putInt(BASE_KEY + \"thing\", target.thing);",
            "    return outState;",
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
            "import icepick.StateHelper;",
            "public class Test$$Icicle implements StateHelper<Bundle> {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  private final StateHelper<Bundle> parent = (StateHelper<Bundle>) StateHelper.NO_OP;",
            "  public Bundle restoreInstanceState(Object obj, Bundle state) {",
            "    Test target = (Test) obj;",
            "    if (state == null) {",
            "      return null;",
            "    }",
            "    Bundle savedInstanceState = state;",
            "    target.thing = savedInstanceState.getFloat(BASE_KEY + \"thing\");",
            "    return parent.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public Bundle saveInstanceState(Object obj, Bundle state) {",
            "    Test target = (Test) obj;",
            "    parent.saveInstanceState(target, state);",
            "    Bundle outState = state;",
            "    outState.putFloat(BASE_KEY + \"thing\", target.thing);",
            "    return outState;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test.TestOne$$Icicle",
        Joiner.on('\n') .join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "import icepick.StateHelper;",
            "public class TestOne$$Icicle implements StateHelper<Bundle> {",
            "  private static final String BASE_KEY = \"test.TestOne$$Icicle.\";",
            "  private final StateHelper<Bundle> parent = new test.Test$$Icicle();",
            "  public Bundle restoreInstanceState(Object obj, Bundle state) {",
            "    TestOne target = (TestOne) obj;",
            "    if (state == null) {",
            "      return null;",
            "    }",
            "    Bundle savedInstanceState = state;",
            "    target.anotherThing = savedInstanceState.getDouble(BASE_KEY + \"anotherThing\");",
            "    return parent.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public Bundle saveInstanceState(Object obj, Bundle state) {",
            "    TestOne target = (TestOne) obj;",
            "    parent.saveInstanceState(target, state);",
            "    Bundle outState = state;",
            "    outState.putDouble(BASE_KEY + \"anotherThing\", target.anotherThing);",
            "    return outState;",
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
        "  @Icicle String thing;",
        "}",
        "class TestOne extends Test<String> {",
        "  @Icicle Long anotherThing;",
        "}",
        "class TestTwo extends Test<Integer> {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "import icepick.StateHelper;",
            "public class Test$$Icicle implements StateHelper<Bundle> {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  private final StateHelper<Bundle> parent = (StateHelper<Bundle>) StateHelper.NO_OP;",
            "  public Bundle restoreInstanceState(Object obj, Bundle state) {",
            "    Test target = (Test) obj;",
            "    if (state == null) {",
            "      return null;",
            "    }",
            "    Bundle savedInstanceState = state;",
            "    target.thing = savedInstanceState.getString(BASE_KEY + \"thing\");",
            "    return parent.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public Bundle saveInstanceState(Object obj, Bundle state) {",
            "    Test target = (Test) obj;",
            "    parent.saveInstanceState(target, state);",
            "    Bundle outState = state;",
            "    outState.putString(BASE_KEY + \"thing\", target.thing);",
            "    return outState;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test.TestOne$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "import icepick.StateHelper;",
            "public class TestOne$$Icicle implements StateHelper<Bundle> {",
            "  private static final String BASE_KEY = \"test.TestOne$$Icicle.\";",
            "  private final StateHelper<Bundle> parent = new test.Test$$Icicle();",
            "  public Bundle restoreInstanceState(Object obj, Bundle state) {",
            "    TestOne target = (TestOne) obj;",
            "    if (state == null) {",
            "      return null;",
            "    }",
            "    Bundle savedInstanceState = state;",
            "    target.anotherThing = savedInstanceState.getLong(BASE_KEY + \"anotherThing\");",
            "    return parent.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public Bundle saveInstanceState(Object obj, Bundle state) {",
            "    TestOne target = (TestOne) obj;",
            "    parent.saveInstanceState(target, state);",
            "    Bundle outState = state;",
            "    outState.putLong(BASE_KEY + \"anotherThing\", target.anotherThing);",
            "    return outState;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void simpleInnerClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  class Inner {",
        "    @Icicle char[] thing;",
        "  }",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test$Inner$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "import icepick.StateHelper;",
            "public class Test$Inner$$Icicle implements StateHelper<Bundle> {",
            "  private static final String BASE_KEY = \"test.Test$Inner$$Icicle.\";",
            "  private final StateHelper<Bundle> parent = (StateHelper<Bundle>) StateHelper.NO_OP;",
            "  public Bundle restoreInstanceState(Object obj, Bundle state) {",
            "    Test.Inner target = (Test.Inner) obj;",
            "    if (state == null) {",
            "      return null;",
            "    }",
            "    Bundle savedInstanceState = state;",
            "    target.thing = savedInstanceState.getCharArray(BASE_KEY + \"thing\");",
            "    return parent.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public Bundle saveInstanceState(Object obj, Bundle state) {",
            "    Test.Inner target = (Test.Inner) obj;",
            "    parent.saveInstanceState(target, state);",
            "    Bundle outState = state;",
            "    outState.putCharArray(BASE_KEY + \"thing\", target.thing);",
            "    return outState;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource);
  } 
  
  @Test public void innerClassRespectsFullyQualifiedClassNames() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  class Inner {",
        "    @Icicle char[] thing;",
        "  }",
        "  class Extender extends Inner {",
        "    @Icicle double[] anotherThing;",
        "  }",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test.Test$Inner$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "import icepick.StateHelper;",
            "public class Test$Inner$$Icicle implements StateHelper<Bundle> {",
            "  private static final String BASE_KEY = \"test.Test$Inner$$Icicle.\";",
            "  private final StateHelper<Bundle> parent = (StateHelper<Bundle>) StateHelper.NO_OP;",
            "  public Bundle restoreInstanceState(Object obj, Bundle state) {",
            "    Test.Inner target = (Test.Inner) obj;",
            "    if (state == null) {",
            "      return null;",
            "    }",
            "    Bundle savedInstanceState = state;",
            "    target.thing = savedInstanceState.getCharArray(BASE_KEY + \"thing\");",
            "    return parent.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public Bundle saveInstanceState(Object obj, Bundle state) {",
            "    Test.Inner target = (Test.Inner) obj;",
            "    parent.saveInstanceState(target, state);",
            "    Bundle outState = state;",
            "    outState.putCharArray(BASE_KEY + \"thing\", target.thing);",
            "    return outState;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test.Test$Extender$$Icicle",
        Joiner.on('\n').join("package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "import icepick.StateHelper;",
            "public class Test$Extender$$Icicle implements StateHelper<Bundle> {",
            "  private static final String BASE_KEY = \"test.Test$Extender$$Icicle.\";",
            "  private final StateHelper<Bundle> parent = new test.Test$Inner$$Icicle();",
            "  public Bundle restoreInstanceState(Object obj, Bundle state) {",
            "    Test.Extender target = (Test.Extender) obj;",
            "    if (state == null) {",
            "      return null;",
            "    }",
            "    Bundle savedInstanceState = state;",
            "    target.anotherThing = savedInstanceState.getDoubleArray(BASE_KEY + \"anotherThing\");",
            "    return parent.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public Bundle saveInstanceState(Object obj, Bundle state) {",
            "    Test.Extender target = (Test.Extender) obj;",
            "    parent.saveInstanceState(target, state);",
            "    Bundle outState = state;",
            "    outState.putDoubleArray(BASE_KEY + \"anotherThing\", target.anotherThing);",
            "    return outState;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void typesAreDowncastWhenNecessary() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
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
        "  @Icicle AL<android.net.Uri> parcelableArrayList;",
        "  @Icicle SA<android.net.Uri> sparseParcelableArray;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "import icepick.StateHelper;",
            "public class Test$$Icicle implements StateHelper<Bundle> {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  private final StateHelper<Bundle> parent = (StateHelper<Bundle>) StateHelper.NO_OP;",
            "  public Bundle restoreInstanceState(Object obj, Bundle state) {",
            "    Test target = (Test) obj;",
            "    if (state == null) {",
            "      return null;",
            "    }",
            "    Bundle savedInstanceState = state;",
            "    target.charSequence = (android.text.SpannableString) savedInstanceState.getCharSequence(BASE_KEY + \"charSequence\");",
            "    target.charSequenceArray = (android.text.SpannableString[]) savedInstanceState.getCharSequenceArray(BASE_KEY + \"charSequenceArray\");",
            "    target.parcelable = (android.accounts.Account) savedInstanceState.getParcelable(BASE_KEY + \"parcelable\");",
            "    target.parcelableArray = (android.accounts.Account[]) savedInstanceState.getParcelableArray(BASE_KEY + \"parcelableArray\");",
            "    target.serializable = (java.io.File) savedInstanceState.getSerializable(BASE_KEY + \"serializable\");",
            "    target.integerArrayList = (test.Test.AL<java.lang.Integer>) savedInstanceState.getIntegerArrayList(BASE_KEY + \"integerArrayList\");",
            "    target.stringArrayList = (test.Test.AL<java.lang.String>) savedInstanceState.getStringArrayList(BASE_KEY + \"stringArrayList\");",
            "    target.charSequenceArrayList = (test.Test.AL<java.lang.CharSequence>) savedInstanceState.getCharSequenceArrayList(BASE_KEY + \"charSequenceArrayList\");",
            "    target.parcelableArrayList = (test.Test.AL<android.net.Uri>) (java.util.ArrayList) savedInstanceState.getParcelableArrayList(BASE_KEY + \"parcelableArrayList\");",
            "    target.sparseParcelableArray = (test.Test.SA<android.net.Uri>) (android.util.SparseArray) savedInstanceState.getSparseParcelableArray(BASE_KEY + \"sparseParcelableArray\");",
            "    return parent.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public Bundle saveInstanceState(Object obj, Bundle state) {",
            "    Test target = (Test) obj;",
            "    parent.saveInstanceState(target, state);",
            "    Bundle outState = state;",
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
            "    return outState;",
            "  }",
            "}"
        ));

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
        // Arrays are Serializable by default
        "  @Icicle Object[] thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError();
  }

  @Test public void viewReturnsParcelable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import icepick.Icicle;",
        "import android.widget.LinearLayout;",
        "import android.content.Context;",
        "public class Test extends LinearLayout {",
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
            "import icepick.StateHelper;",
            "public class Test$$Icicle implements StateHelper<Parcelable> {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  private final StateHelper<Parcelable> parent = (StateHelper<Parcelable>) StateHelper.NO_OP;",
            "  public Parcelable restoreInstanceState(Object obj, Parcelable state) {",
            "    Test target = (Test) obj;",
            "    Bundle savedInstanceState = (Bundle) state;",
            "    Parcelable superState = savedInstanceState.getParcelable(BASE_KEY + \"$$SUPER$$\");",
            "    target.thing = savedInstanceState.getBundle(BASE_KEY + \"thing\");",
            "    return parent.restoreInstanceState(target, superState);",
            "  }",
            "  public Parcelable saveInstanceState(Object obj, Parcelable state) {",
            "    Test target = (Test) obj;",
            "    Bundle outState = new Bundle();",
            "    Parcelable superState = parent.saveInstanceState(target, state);",
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

  private Iterable<? extends Processor> icepickProcessors() {
    return Arrays.asList(new IcepickProcessor());
  }
}
