package icepick.processor;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;

import java.util.Arrays;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

/**
 * Created by uniqa on 28.10.14.
 */
public class IcepickSafetyTest {
    @Test
    public void nullAndPrimitiveTypeSafety() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
                "package test;\n" +
                "import icepick.Icicle;\n" +
                "import javax.annotation.Nonnull;\n" +
                "import java.lang.annotation.ElementType;\n" +
                "import java.lang.annotation.Retention;\n" +
                "import java.lang.annotation.RetentionPolicy;\n" +
                "import java.lang.annotation.Target;\n" +
                "\n" +
                "public class Test<T> {\n" +
                "    @Icicle\n" +
                "    Long looong;\n" +
                "\n" +
                "    @Icicle\n" +
                "    Short s;\n" +
                "\n" +
                "    @Icicle\n" +
                "    Boolean bool;\n" +
                "\n" +
                "    @Icicle\n" +
                "    Integer integer;\n" +
                "\n" +
                "    @Icicle\n" +
                "    Character character;\n" +
                "\n" +
                "    @Icicle\n" +
                "    Double x2;\n" +
                "\n" +
                "    @Icicle\n" +
                "    Float $;\n" +
                "\n" +
                "    @Icicle\n" +
                "    Byte b;\n" +
                "\n" +
                "    @Icicle @Nonnull\n" +
                "    Float $$;\n" +
                "\n" +
                "    @Icicle @javax.annotation.Nullable\n" +
                "    CharSequence wow;\n" +
                "\n" +
                "    @Icicle @Nullable\n" +
                "    String nullable;\n" +
                "\n" +
                "    @Icicle\n" +
                "    String nullSafe;\n" +
                "}\n" +
                "@Target(ElementType.FIELD)\n" +
                "@Retention(RetentionPolicy.CLASS)\n" +
                "@interface Nullable {\n" +
                "}");

        JavaFileObject expectedSource =
                JavaFileObjects.forSourceString("test.Test$$Icicle",
                        "package test;\n" +
                        "import android.os.Bundle;\n" +
                        "import android.os.Parcelable;\n" +
                        "import icepick.StateHelper;\n" +
                        "public class Test$$Icicle implements StateHelper<Bundle> {\n" +
                        "\n" +
                        "  private static final String BASE_KEY = \"test.Test$$Icicle.\";\n" +
                        "  private final StateHelper<Bundle> parent = (StateHelper<Bundle>) StateHelper.NO_OP;\n" +
                        "\n" +
                        "  public Bundle restoreInstanceState(Object obj, Bundle state) {\n" +
                        "    Test target = (Test) obj;\n" +
                        "    if (state == null) {\n" +
                        "      return null;\n" +
                        "    }\n" +
                        "    Bundle savedInstanceState = state;\n" +
                        "\n" +
                        "    if (savedInstanceState.containsKey(BASE_KEY + \"looong\"))\n" +
                        "        target.looong =  savedInstanceState.getLong(BASE_KEY + \"looong\");\n" +
                        "    if (savedInstanceState.containsKey(BASE_KEY + \"s\"))\n" +
                        "        target.s =  savedInstanceState.getShort(BASE_KEY + \"s\");\n" +
                        "    if (savedInstanceState.containsKey(BASE_KEY + \"bool\"))\n" +
                        "        target.bool =  savedInstanceState.getBoolean(BASE_KEY + \"bool\");\n" +
                        "    if (savedInstanceState.containsKey(BASE_KEY + \"integer\"))\n" +
                        "        target.integer =  savedInstanceState.getInt(BASE_KEY + \"integer\");\n" +
                        "    if (savedInstanceState.containsKey(BASE_KEY + \"character\"))\n" +
                        "        target.character =  savedInstanceState.getChar(BASE_KEY + \"character\");\n" +
                        "    if (savedInstanceState.containsKey(BASE_KEY + \"x2\"))\n" +
                        "        target.x2 =  savedInstanceState.getDouble(BASE_KEY + \"x2\");\n" +
                        "    if (savedInstanceState.containsKey(BASE_KEY + \"$\"))\n" +
                        "        target.$ =  savedInstanceState.getFloat(BASE_KEY + \"$\");\n" +
                        "    if (savedInstanceState.containsKey(BASE_KEY + \"b\"))\n" +
                        "        target.b =  savedInstanceState.getByte(BASE_KEY + \"b\");\n" +
                        "    target.$$ =  savedInstanceState.getFloat(BASE_KEY + \"$$\");\n" +
                        "    if (savedInstanceState.containsKey(BASE_KEY + \"wow\"))\n" +
                        "        target.wow = (java.lang.CharSequence) savedInstanceState.getCharSequence(BASE_KEY + \"wow\");\n" +
                        "    if (savedInstanceState.containsKey(BASE_KEY + \"nullable\"))\n" +
                        "        target.nullable =  savedInstanceState.getString(BASE_KEY + \"nullable\");\n" +
                        "    target.nullSafe =  savedInstanceState.getString(BASE_KEY + \"nullSafe\");\n" +
                        "\n" +
                        "    return parent.restoreInstanceState(target, savedInstanceState);\n" +
                        "  }\n" +
                        "  public Bundle saveInstanceState(Object obj, Bundle state) {\n" +
                        "    Test target = (Test) obj;\n" +
                        "    parent.saveInstanceState(target, state);\n" +
                        "    Bundle outState = state;\n" +
                        "\n" +
                        "    if (target.looong != null)\n" +
                        "        outState.putLong(BASE_KEY + \"looong\", target.looong);\n" +
                        "    if (target.s != null)\n" +
                        "        outState.putShort(BASE_KEY + \"s\", target.s);\n" +
                        "    if (target.bool != null)\n" +
                        "        outState.putBoolean(BASE_KEY + \"bool\", target.bool);\n" +
                        "    if (target.integer != null)\n" +
                        "        outState.putInt(BASE_KEY + \"integer\", target.integer);\n" +
                        "    if (target.character != null)\n" +
                        "        outState.putChar(BASE_KEY + \"character\", target.character);\n" +
                        "    if (target.x2 != null)\n" +
                        "        outState.putDouble(BASE_KEY + \"x2\", target.x2);\n" +
                        "    if (target.$ != null)\n" +
                        "        outState.putFloat(BASE_KEY + \"$\", target.$);\n" +
                        "    if (target.b != null)\n" +
                        "        outState.putByte(BASE_KEY + \"b\", target.b);\n" +
                        "    outState.putFloat(BASE_KEY + \"$$\", target.$$);\n" +
                        "    if (target.wow != null)\n" +
                        "        outState.putCharSequence(BASE_KEY + \"wow\", target.wow);\n" +
                        "    if (target.nullable != null)\n" +
                        "        outState.putString(BASE_KEY + \"nullable\", target.nullable);\n" +
                        "    outState.putString(BASE_KEY + \"nullSafe\", target.nullSafe);\n" +
                        "\n" +
                        "    return outState;\n" +
                        "  }\n" +
                        "}");

        ASSERT.about(javaSource()).that(source)
                .processedWith(icepickProcessors())
                .compilesWithoutError()
                .and().generatesSources(expectedSource);
    }

    private Iterable<? extends Processor> icepickProcessors() {
        return Arrays.asList(new IcepickProcessor());
    }
}
