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

    static final String SUFFIX = "$$Icicle";
    static final String PACKAGE = "com.frankiesardo";
    static final String SIMPLE_NAME = "TestActivity";
    static final String QUALIFIED_NAME = PACKAGE + "." + SIMPLE_NAME;
    static final IcicleField ICICLE_FIELD = new IcicleField("username", "java.lang.String", "String");
    StringWriter stringWriter = new StringWriter();

    IcicleViewWriter icicleWriter = new IcicleViewWriter(stringWriter, SUFFIX);
    TypeElement classType = mock(TypeElement.class, RETURNS_DEEP_STUBS);

    Set<IcicleField> fields = new LinkedHashSet<IcicleField>();

    @Before
    public void setUp() throws Exception {
        when(classType.getQualifiedName().toString()).thenReturn(QUALIFIED_NAME);
        when(classType.getSimpleName().toString()).thenReturn(SIMPLE_NAME);

        fields.add(ICICLE_FIELD);
    }

    @Test
    public void shouldWriteBundlePutAndGetIntoATemplate() throws Exception {
        icicleWriter.writeClass(classType, fields);

        assertEquals(SIMPLE_CLASS, stringWriter.toString());
    }

    static final String SIMPLE_CLASS = "package " + PACKAGE + ";\n" +
            "\n" +
            "final class " + SIMPLE_NAME + SUFFIX + " {\n" +
            "  private static final String BASE_KEY = \"" + PACKAGE + "." + SIMPLE_NAME + SUFFIX + ".\";\n\n" +
            "  private static final String SUPER_SUFFIX = \"$$SUPER$$\";\n\n" +
            "  private " + SIMPLE_NAME + SUFFIX + "() {\n" +
            "  }\n" +
            "  public static android.os.Parcelable saveInstanceState(" + SIMPLE_NAME + " target, android.os.Parcelable state) {\n" +
            "    android.os.Bundle outState = new android.os.Bundle();\n" +
            "    outState.putParcelable(BASE_KEY + SUPER_SUFFIX, state);\n" +
            "    outState.put" + ICICLE_FIELD.getCommand() + "(BASE_KEY + \"" + ICICLE_FIELD.getName() + "\", target." + ICICLE_FIELD.getName() + ");\n" +
            "    return outState;\n" +
            "  }\n" +
            "  public static android.os.Parcelable restoreInstanceState(" + SIMPLE_NAME + " target, android.os.Parcelable state) {\n" +
            "    android.os.Bundle savedInstanceState = (android.os.Bundle) state;\n" +
            "    target." + ICICLE_FIELD.getName() + " = savedInstanceState.get" + ICICLE_FIELD.getCommand() + "(BASE_KEY + \"" + ICICLE_FIELD.getName() + "\");\n" +
            "    return savedInstanceState.getParcelable(BASE_KEY + SUPER_SUFFIX);\n" +
            "  }\n" +
            "}\n";
}
