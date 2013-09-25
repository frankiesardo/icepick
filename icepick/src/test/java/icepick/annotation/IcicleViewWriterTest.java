package icepick.annotation;

import javax.lang.model.element.TypeElement;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class IcicleViewWriterTest {

    StringWriter stringWriter = new StringWriter();
    IcicleViewWriter icicleWriter = new IcicleViewWriter(stringWriter, "$$Icicle");
    IcicleEnclosingClass icicleEnclosingClass = new IcicleEnclosingClass(mock(TypeElement.class), "TestView", "com.frankiesardo", "com.frankiesardo.SuperClass");

    Set<IcicleField> fields = new LinkedHashSet<IcicleField>();

    @Before
    public void setUp() throws Exception {
        IcicleField icicleField = new IcicleField("username", "java.lang.String", "String");
        fields.add(icicleField);
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
