package icepick.processor;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Ignore;
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

  @Ignore
  @Test public void injectsArbitraryObjects() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n') .join(
        "package test;",
        "import icepick.Icicle;",
        "public class Test {",
        "  @Icicle Object thing;",
        "}"));

    JavaFileObject expectedHelper =
        JavaFileObjects.forSourceString("test.Test$$Icicle", Joiner.on("\n") .join(
            "package test;",
            "import static icepick.Icepick.wrap;",
            "import static icepick.Icepick.unwrap;",
            "import android.os.Bundle;",
            "public class Test$$Icicle {",
            "  private static final String BASE_KEY = \"test.Test$$Icicle.\";",
            "  public static void restoreInstanceState(Target target, Bundle savedInstanceState) {",
            "    if (savedInstanceState == null) {",
            "      return;",
            "    }",
            "    target.thing = unwrap(savedInstanceState.getParcelable(BASE_KEY + \"thing\"));",
            "  }",
            "",
            "  public static void saveInstanceState(Target target, Bundle outState) {",
            "    outState.putParcelable(BASE_KEY + \"thing\", wrap(target.thing));",
            "  }",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(icepickProcessors())
        .compilesWithoutError()
        .and().generatesSources(expectedHelper);
  }
}
