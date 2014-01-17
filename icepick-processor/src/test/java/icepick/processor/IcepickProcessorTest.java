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
}
