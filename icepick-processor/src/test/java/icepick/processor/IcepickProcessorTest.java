package icepick.processor;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import java.util.Arrays;

import org.junit.Test;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class IcepickProcessorTest {

  @Test public void fieldsMustNotBePrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle private Object thing;",
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
        "  @Icicle static Object thing;",
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
        "  @Icicle final Object thing;",
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
        "    @Icicle final Object thing;",
        "  }",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .failsToCompile();
  }

  @Test public void simple() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle Object thing;",
        "}"
    ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test.Test$$Icicle", Joiner.on("\n") .join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Test target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"thing\"));",
            "  }", "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putParcelable(BASE_KEY + \"thing\", wrap(target.thing));", "  }",
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
        "  @Icicle Object thing;",
        "}",
        "class TestOne extends Test {",
        "  @Icicle Object anotherThing;",
        "}",
        "class TestTwo extends Test {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Test target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"thing\"));",
            "  }",
            "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putParcelable(BASE_KEY + \"thing\", wrap(target.thing));",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test.TestOne$$Icicle",
        Joiner.on('\n') .join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class TestOne$$Icicle {",
            "  private static final String BASE_KEY = \"test.TestOne$$Icicle.\";",
            "  public static void restoreInstanceState(TestOne target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.anotherThing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"anotherThing\"));",
            "    test.Test$$Icicle.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public static void saveInstanceState(TestOne target, Bundle outState) {",
            "    test.Test$$Icicle.saveInstanceState(target, outState);",
            "    outState.putParcelable(BASE_KEY + \"anotherThing\", wrap(target.anotherThing));",
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
        "  @Icicle Object thing;",
        "}",
        "class TestOne extends Test<String> {",
        "  @Icicle Object anotherThing;",
        "}",
        "class TestTwo extends Test<Integer> {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Test target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"thing\"));",
            "  }",
            "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putParcelable(BASE_KEY + \"thing\", wrap(target.thing));",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test.TestOne$$Icicle",
        Joiner.on('\n') .join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class TestOne$$Icicle {",
            "  private static final String BASE_KEY = \"test.TestOne$$Icicle.\";",
            "  public static void restoreInstanceState(TestOne target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.anotherThing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"anotherThing\"));",
            "    test.Test$$Icicle.restoreInstanceState(target, savedInstanceState);",
            "  }",
            "  public static void saveInstanceState(TestOne target, Bundle outState) {",
            "    test.Test$$Icicle.saveInstanceState(target, outState);",
            "    outState.putParcelable(BASE_KEY + \"anotherThing\", wrap(target.anotherThing));",
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
        "    @Icicle Object thing;",
        "  }",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test$Inner$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$Inner$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$Inner$$Icicle.\";",
            "  public static void restoreInstanceState(Test.Inner target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"thing\"));",
            "  }",
            "  public static void saveInstanceState(Test.Inner target, Bundle outState) {",
            "    outState.putParcelable(BASE_KEY + \"thing\", wrap(target.thing));",
            "  }",
            "}"
        ));
    
    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource);
  }

  @Test public void serializableIsPersistedAsIs() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle java.io.File thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Test target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = (java.io.File) savedInstanceState.getSerializable(BASE_KEY + \"thing\");",
            "  }",
            "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putSerializable(BASE_KEY + \"thing\", target.thing);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource);
  }

  @Test public void parcelableIsPersistedAsIs() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle android.os.Bundle thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n').join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Test target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = (android.os.Bundle) savedInstanceState.getParcelable(BASE_KEY + \"thing\");",
            "  }",
            "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putParcelable(BASE_KEY + \"thing\", target.thing);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource);
  }

  @Test public void primitivesAndArraysAreHandledByCustomWrapping() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n')
        .join("package test;",
            "import icepick.Icicle;",
            "public class Test {",
            "  @Icicle float thing;",
            "  @Icicle int[] anotherThing;",
            "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on("\n").join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Test target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"thing\"));",
            "    target.anotherThing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"anotherThing\"));",
            "  }",
            "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putParcelable(BASE_KEY + \"thing\", wrap(target.thing));",
            "    outState.putParcelable(BASE_KEY + \"anotherThing\", wrap(target.anotherThing));",
            "  }",
            "}"
        ));

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
        "  @Icicle Object thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test$$Icicle",
        Joiner.on('\n') .join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static Parcelable restoreInstanceState(Test target, Parcelable state) {",
            "    Bundle savedInstanceState = (Bundle)state;",
            "    Parcelable superState = savedInstanceState.getParcelable(BASE_KEY + \"$$SUPER$$\");",
            "    target.thing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"thing\"));",
            "    return superState;",
            "  }",
            "  public static Parcelable saveInstanceState(Test target, Parcelable state) {",
            "    Bundle outState = new Bundle();",
            "    Parcelable superState = state;",
            "    outState.putParcelable(BASE_KEY + \"$$SUPER$$\", superState);",
            "    outState.putParcelable(BASE_KEY + \"thing\", wrap(target.thing));",
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
        "  @Icicle Object thing;",
        "}",
        "class TestOne extends Test {",
        "  public TestOne(Context context) {",
        "    super(context);",
        "  }",
        "  @Icicle Object anotherThing;",
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
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static Parcelable restoreInstanceState(Test target, Parcelable state) {",
            "    Bundle savedInstanceState = (Bundle)state;",
            "    Parcelable superState = savedInstanceState.getParcelable(BASE_KEY + \"$$SUPER$$\");",
            "    target.thing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"thing\"));",
            "    return superState;",
            "  }",
            "  public static Parcelable saveInstanceState(Test target, Parcelable state) {",
            "    Bundle outState = new Bundle();",
            "    Parcelable superState = state;",
            "    outState.putParcelable(BASE_KEY + \"$$SUPER$$\", superState);",
            "    outState.putParcelable(BASE_KEY + \"thing\", wrap(target.thing));",
            "    return outState;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test.TestOne$$Icicle",
        Joiner.on('\n') .join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "import android.os.Parcelable;",
            "public class TestOne$$Icicle {",
            "  private static final String BASE_KEY = \"test.TestOne$$Icicle.\";",
            "  public static Parcelable restoreInstanceState(TestOne target, Parcelable state) {",
            "    Bundle savedInstanceState = (Bundle)state;",
            "    Parcelable superState = savedInstanceState.getParcelable(BASE_KEY + \"$$SUPER$$\");",
            "    target.anotherThing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"anotherThing\"));",
            "    return test.Test$$Icicle.restoreInstanceState(target, superState);",
            "  }",
            "  public static Parcelable saveInstanceState(TestOne target, Parcelable state) {",
            "    Bundle outState = new Bundle();",
            "    Parcelable superState = test.Test$$Icicle.saveInstanceState(target, state);",
            "    outState.putParcelable(BASE_KEY + \"$$SUPER$$\", superState);",
            "    outState.putParcelable(BASE_KEY + \"anotherThing\", wrap(target.anotherThing));",
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
