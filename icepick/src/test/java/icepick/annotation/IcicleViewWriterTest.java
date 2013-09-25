package icepick.annotation;

import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IcicleViewWriterTest {

    final StringWriter stringWriter = new StringWriter();
    final JavaFileObject jfo = mock(JavaFileObject.class);
    final IcicleViewWriter icicleWriter = new IcicleViewWriter(jfo, "$$Icicle");
    final IcicleEnclosingClass icicleEnclosingClass = new IcicleEnclosingClass(mock(TypeElement.class), "TestView", "com.frankiesardo", "com.frankiesardo.SuperClass");

    final Set<IcicleField> fields = new LinkedHashSet<IcicleField>();

    @Before
    public void setUp() throws Exception {
        IcicleField icicleField = new IcicleField("username", "", "String");
        fields.add(icicleField);

        when(jfo.openWriter()).thenReturn(stringWriter);
    }

    @Test
    public void shouldWriteBundlePutAndGetIntoATemplate() throws Exception {
        icicleWriter.writeClass(icicleEnclosingClass, fields);

        assertEquals(SIMPLE_CLASS, stringWriter.toString());
    }

    static final String SIMPLE_CLASS = "package com.frankiesardo;\n" +
            "\n" +
            "final class TestView$$Icicle {\n" +
            "\n" +
            "  private static final String BASE_KEY = \"com.frankiesardo.TestView$$Icicle.\";\n" +
            "\n" +
            "  private TestView$$Icicle() {\n" +
            "  }\n" +
            "\n" +
            "  public static android.os.Parcelable saveInstanceState(" + "TestView" + " target, android.os.Parcelable state) {\n" +
            "    android.os.Bundle outState = new android.os.Bundle();\n" +
            "    android.os.Parcelable superState = com.frankiesardo.SuperClass$$Icicle.saveInstanceState(target, state);\n" +
            "    outState.putParcelable(" + IcicleWriter.BASE_KEY + " + " + IcicleViewWriter.SUPER_SUFFIX + ", superState);\n" +
            "    outState.putString(" + IcicleWriter.BASE_KEY + " + \"username\", target.username);\n" +
            "    return outState;\n" +
            "  }\n" +
            "\n" +
            "  public static android.os.Parcelable restoreInstanceState(" + "TestView" + " target, android.os.Parcelable state) {\n" +
            "    android.os.Bundle savedInstanceState = (android.os.Bundle) state;\n" +
            "    android.os.Parcelable superState = savedInstanceState.getParcelable(" + IcicleWriter.BASE_KEY + " + " + IcicleViewWriter.SUPER_SUFFIX + ");\n" +
            "    target.username = savedInstanceState.getString(" + IcicleWriter.BASE_KEY + " + \"username\");\n" +
            "    return com.frankiesardo.SuperClass$$Icicle.restoreInstanceState(target, superState);\n" +
            "  }\n" +
            "}\n";
}
