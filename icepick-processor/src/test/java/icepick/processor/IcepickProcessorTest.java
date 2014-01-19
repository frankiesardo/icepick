package icepick.processor;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static icepick.processor.ProcessorTestUtilities.icepickProcessors;
import static org.truth0.Truth.ASSERT;

public class IcepickProcessorTest {

  @Test public void fieldsMustNotBePrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle private Object thing;",
        "}"));

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
        "}"));

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
        "}"));

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
        "  @Icicle Object thing;",
        "}"));

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
            "  }",
            "  public static void saveInstanceState(Test target, Bundle outState) {",
            "    outState.putParcelable(BASE_KEY + \"thing\", wrap(target.thing));",
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
            "}"));

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
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedSource1, expectedSource2);
  }
}
