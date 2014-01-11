package icepick.processor;

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

public class ViewWriterTest {

    final StringWriter stringWriter = new StringWriter();
    final JavaFileObject jfo = mock(JavaFileObject.class);
    final ViewWriter icicleWriter = new ViewWriter(jfo, "$$Icicle");
    final FieldEnclosingClass fieldEnclosingClass = new FieldEnclosingClass(mock(TypeElement.class), "TestView", "TestView", "com.frankiesardo", "com.frankiesardo.SuperClass");

    final Set<AnnotatedField> fields = new LinkedHashSet<AnnotatedField>();

    @Before
    public void setUp() throws Exception {
        AnnotatedField annotatedField = new AnnotatedField("username", "", "String");
        fields.add(annotatedField);

        when(jfo.openWriter()).thenReturn(stringWriter);
    }

    @Test
    public void shouldWriteBundlePutAndGetIntoATemplate() throws Exception {
        icicleWriter.writeClass(fieldEnclosingClass, fields);

        assertEquals(SIMPLE_CLASS, stringWriter.toString());
    }

    static final String SIMPLE_CLASS = "package com.frankiesardo;\n" +
            "\n" +
            "public final class TestView$$Icicle {\n" +
            "\n" +
            "  private static final String BASE_KEY = \"com.frankiesardo.TestView$$Icicle.\";\n" +
            "\n" +
            "  private TestView$$Icicle() {\n" +
            "  }\n" +
            "\n" +
            "  public static android.os.Parcelable saveInstanceState(" + "TestView" + " target, android.os.Parcelable state) {\n" +
            "    android.os.Bundle outState = new android.os.Bundle();\n" +
            "    android.os.Parcelable superState = com.frankiesardo.SuperClass$$Icicle.saveInstanceState(target, state);\n" +
            "    outState.putParcelable(" + ClassWriter.BASE_KEY + " + " + ViewWriter.SUPER_SUFFIX + ", superState);\n" +
            "    outState.putString(" + ClassWriter.BASE_KEY + " + \"username\", target.username);\n" +
            "    return outState;\n" +
            "  }\n" +
            "\n" +
            "  public static android.os.Parcelable restoreInstanceState(" + "TestView" + " target, android.os.Parcelable state) {\n" +
            "    android.os.Bundle savedInstanceState = (android.os.Bundle) state;\n" +
            "    android.os.Parcelable superState = savedInstanceState.getParcelable(" + ClassWriter.BASE_KEY + " + " + ViewWriter.SUPER_SUFFIX + ");\n" +
            "    target.username = savedInstanceState.getString(" + ClassWriter.BASE_KEY + " + \"username\");\n" +
            "    return com.frankiesardo.SuperClass$$Icicle.restoreInstanceState(target, superState);\n" +
            "  }\n" +
            "}\n";
}
