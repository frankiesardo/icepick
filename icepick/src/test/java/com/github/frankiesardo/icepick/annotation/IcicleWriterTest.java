package com.github.frankiesardo.icepick.annotation;

import com.squareup.java.JavaWriter;

import javax.lang.model.element.TypeElement;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class IcicleWriterTest {

    static final String SUFFIX = "$$Icicle";
    static final String PACKAGE = "com.frankiesardo";
    static final String SIMPLE_NAME = "TestActivity";
    static final String QUALIFIED_NAME = PACKAGE + "." + SIMPLE_NAME;
    StringWriter stringWriter = new StringWriter();

    JavaWriter javaWriter = new JavaWriter(stringWriter);
    IcicleWriter icicleWriter = new IcicleWriter(javaWriter, SUFFIX);
    TypeElement classType = mock(TypeElement.class, RETURNS_DEEP_STUBS);

    Set<IcicleField> fields = new LinkedHashSet<IcicleField>();
    static final IcicleField ICICLE_FIELD = new IcicleField("username", "somekey", "java.lang.String", "String");

    @Before
    public void setUp() throws Exception {
        when(classType.getQualifiedName().toString()).thenReturn(QUALIFIED_NAME);
        when(classType.getSimpleName().toString()).thenReturn(SIMPLE_NAME);

        fields.add(ICICLE_FIELD);
    }

    @Test
    public void insertFieldPutAndGetIntoATemplate() throws Exception {
        icicleWriter.writeClass(classType, fields);

        assertEquals(SIMPLE_CLASS, stringWriter.toString());
    }

    static final String SIMPLE_CLASS = "package " + PACKAGE + ";\n" +
            "\n" +
            "final class " + SIMPLE_NAME + SUFFIX + " {\n" +
            "  private " + SIMPLE_NAME + SUFFIX + "() {\n" +
            "  }\n" +
            "  public static void saveInstanceState(" + SIMPLE_NAME + " target, android.os.Bundle outState) {\n" +
            "    outState.put" + ICICLE_FIELD.getCommand() + "(\"" + ICICLE_FIELD.getKey() + "\", target." + ICICLE_FIELD.getName() + ");\n" +
            "  }\n" +
            "  public static void restoreInstanceState(" + SIMPLE_NAME + " target, android.os.Bundle savedInstanceState) {\n" +
            "    if (savedInstanceState == null) {\n" +
            "      return;\n" +
            "    }\n" +
            "    target." + ICICLE_FIELD.getName() + " = savedInstanceState.get" + ICICLE_FIELD.getCommand() + "(\"" + ICICLE_FIELD.getKey() + "\");\n" +
            "  }\n" +
            "}";
}
