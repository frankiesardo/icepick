package com.github.frankiesardo.icepick.annotation;

import javax.lang.model.element.TypeElement;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class IcicleViewWriterTest {

    StringWriter stringWriter = new StringWriter();
    IcicleViewWriter icicleWriter = new IcicleViewWriter(stringWriter, "$$Icicle");
    TypeElement classType = mock(TypeElement.class, RETURNS_DEEP_STUBS);

    Set<IcicleField> fields = new LinkedHashSet<IcicleField>();

    @Before
    public void setUp() throws Exception {
        IcicleField icicleField = new IcicleField("username", "java.lang.String", "String");
        when(classType.getQualifiedName().toString()).thenReturn("com.frankiesardo" + "." + "TestActivity");
        when(classType.getSimpleName().toString()).thenReturn("TestActivity");

        fields.add(icicleField);
    }

    @Test
    public void shouldWriteBundlePutAndGetIntoATemplate() throws Exception {
        icicleWriter.writeClass(classType, fields);

        assertEquals(SIMPLE_CLASS, stringWriter.toString());
    }

    static final String SIMPLE_CLASS = "package com.frankiesardo;\n" +
            "\n" +
            "final class TestActivity$$Icicle {\n" +
            "\n" +
            "  private static final String BASE_KEY = \"com.frankiesardo.TestActivity$$Icicle.\";\n" +
            "\n" +
            "  private TestActivity$$Icicle() {\n" +
            "  }\n" +
            "\n" +
            "  public static android.os.Parcelable saveInstanceState(" + "TestActivity" + " target, android.os.Parcelable state) {\n" +
            "    android.os.Bundle outState = new android.os.Bundle();\n" +
            "    android.os.Parcelable superState = state;\n" +
            "    outState.putParcelable(" + IcicleWriter.BASE_KEY + " + " + IcicleViewWriter.SUPER_SUFFIX + ", superState);\n" +
            "    outState.putString(" + IcicleWriter.BASE_KEY + " + \"username\", target.username);\n" +
            "    return outState;\n" +
            "  }\n" +
            "\n" +
            "  public static android.os.Parcelable restoreInstanceState(" + "TestActivity" + " target, android.os.Parcelable state) {\n" +
            "    android.os.Bundle savedInstanceState = (android.os.Bundle) state;\n" +
            "    android.os.Parcelable superState = savedInstanceState.getParcelable(" + IcicleWriter.BASE_KEY + " + " + IcicleViewWriter.SUPER_SUFFIX + ");\n" +
            "    target.username = savedInstanceState.getString(" + IcicleWriter.BASE_KEY + " + \"username\");\n" +
            "    return superState;\n" +
            "  }\n" +
            "}\n";
}
