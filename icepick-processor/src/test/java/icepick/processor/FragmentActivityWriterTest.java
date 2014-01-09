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

public class FragmentActivityWriterTest {

    final StringWriter stringWriter = new StringWriter();
    final JavaFileObject jfo = mock(JavaFileObject.class);
    final ClassWriter classWriter = new FragmentActivityWriter(jfo, "$$Icicle");
    final FieldEnclosingClass fieldEnclosingClass = new FieldEnclosingClass(mock(TypeElement.class), "TestActivity", "com.frankiesardo", "com.frankiesardo.SuperClass");

    final Set<AnnotatedField> fields = new LinkedHashSet<AnnotatedField>();

    @Before
    public void setUp() throws Exception {
        AnnotatedField annotatedField = new AnnotatedField("username", "", "String");
        fields.add(annotatedField);

        when(jfo.openWriter()).thenReturn(stringWriter);
    }

    @Test
    public void shouldWriteBundlePutAndGetIntoATemplate() throws Exception {
        classWriter.writeClass(fieldEnclosingClass, fields);

        assertEquals(SIMPLE_CLASS, stringWriter.toString());
    }

    static final String SIMPLE_CLASS = "package com.frankiesardo;\n" +
            "\n" +
            "public final class TestActivity$$Icicle {\n" +
            "\n" +
            "  private static final String BASE_KEY = \"com.frankiesardo.TestActivity$$Icicle.\";\n" +
            "\n" +
            "  private TestActivity$$Icicle() {\n" +
            "  }\n" +
            "\n" +
            "  public static void saveInstanceState(TestActivity target, android.os.Bundle outState) {\n" +
            "    com.frankiesardo.SuperClass$$Icicle.saveInstanceState(target, outState);\n" +
            "    outState.putString(" + ClassWriter.BASE_KEY + " + \"username\", target.username);\n" +
            "  }\n" +
            "\n" +
            "  public static void restoreInstanceState(TestActivity target, android.os.Bundle savedInstanceState) {\n" +
            "    if (savedInstanceState == null) {\n" +
            "      return;\n" +
            "    }\n" +
            "    target.username = savedInstanceState.getString(" + ClassWriter.BASE_KEY + " + \"username\");\n" +
            "    com.frankiesardo.SuperClass$$Icicle.restoreInstanceState(target, savedInstanceState);\n" +
            "  }\n" +
            "}\n";
}
