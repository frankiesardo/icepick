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

public class IcicleFragmentActivityWriterTest {

    final StringWriter stringWriter = new StringWriter();
    final JavaFileObject jfo = mock(JavaFileObject.class);
    final IcicleWriter icicleWriter = new IcicleFragmentActivityWriter(jfo, "$$Icicle");
    final IcicleEnclosingClass icicleEnclosingClass = new IcicleEnclosingClass(mock(TypeElement.class), "TestActivity", "com.frankiesardo", "com.frankiesardo.SuperClass");

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
            "public final class TestActivity$$Icicle {\n" +
            "\n" +
            "  private static final String BASE_KEY = \"com.frankiesardo.TestActivity$$Icicle.\";\n" +
            "\n" +
            "  private TestActivity$$Icicle() {\n" +
            "  }\n" +
            "\n" +
            "  public static void saveInstanceState(TestActivity target, android.os.Bundle outState) {\n" +
            "    com.frankiesardo.SuperClass$$Icicle.saveInstanceState(target, outState);\n" +
            "    outState.putString(" + IcicleWriter.BASE_KEY + " + \"username\", target.username);\n" +
            "  }\n" +
            "\n" +
            "  public static void restoreInstanceState(TestActivity target, android.os.Bundle savedInstanceState) {\n" +
            "    if (savedInstanceState == null) {\n" +
            "      return;\n" +
            "    }\n" +
            "    target.username = savedInstanceState.getString(" + IcicleWriter.BASE_KEY + " + \"username\");\n" +
            "    com.frankiesardo.SuperClass$$Icicle.restoreInstanceState(target, savedInstanceState);\n" +
            "  }\n" +
            "}\n";
}
