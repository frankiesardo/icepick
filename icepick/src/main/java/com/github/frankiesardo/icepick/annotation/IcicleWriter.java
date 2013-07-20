package com.github.frankiesardo.icepick.annotation;

import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

class IcicleWriter {

    private final Writer writer;
    private final String suffix;

    public IcicleWriter(Writer writer, String suffix) {
        this.writer = writer;
        this.suffix = suffix;
    }

    public void writeClass(TypeElement classType, Set<IcicleField> fields) throws IOException {
        String packageName = makePackage(classType);
        String className = makeType(classType);
        String saveInstanceState = writeOnSaveInstanceState(fields);
        String restoreInstanceState = makeOnRestoreInstanceState(fields);
        writeTemplateWith(packageName, className, saveInstanceState, restoreInstanceState);
    }

    private void writeTemplateWith(String packageName, String className, String saveInstanceState, String restoreInstanceState) throws IOException {
        String result = CLASS_TEMPLATE
                .replace(PACKAGE, packageName)
                .replace(CLASS_NAME, className)
                .replace(SUFFIX, suffix)
                .replace(SAVEINSTANCESTATE, saveInstanceState)
                .replace(RESTOREINSTANCESTATE, restoreInstanceState);
        writer.write(result);
        writer.flush();
        writer.close();
    }

    private String makePackage(TypeElement classType) {
        return classType.getQualifiedName().toString().replace("." + classType.getSimpleName(), "");
    }

    private String makeType(TypeElement classType) {
        return classType.getSimpleName().toString();
    }

    private String makeOnRestoreInstanceState(Set<IcicleField> fields) {
        StringBuilder builder = new StringBuilder();
        for (IcicleField field : fields) {
            builder.append(makeBundleGet(field));
        }
        return builder.toString();
    }

    private String makeBundleGet(IcicleField icicleField) {
        return RESTOREINSTANCESTATE_TEMPLATE
                .replace(NAME, icicleField.getName())
                .replace(KEY, icicleField.getKey())
                .replace(COMMAND, icicleField.getCommand())
                .replace(CAST, icicleField.getTypeCast());
    }

    private String writeOnSaveInstanceState(Set<IcicleField> fields) {
        StringBuilder builder = new StringBuilder();
        for (IcicleField field : fields) {
            builder.append(makeBundlePut(field));
        }
        return builder.toString();
    }

    private String makeBundlePut(IcicleField icicleField) {
        return SAVEINSTANCESTATE_TEMPLATE
                .replace(NAME, icicleField.getName())
                .replace(KEY, icicleField.getKey())
                .replace(COMMAND, icicleField.getCommand());
    }

    private static final String PACKAGE = "{packageName}";
    private static final String CLASS_NAME = "{className}";
    private static final String SUFFIX = "{suffix}";
    private static final String SAVEINSTANCESTATE = "{saveInstanceState}";
    private static final String RESTOREINSTANCESTATE = "{restoreInstanceState}";
    private static final String CLASS_TEMPLATE = "package " + PACKAGE + ";\n" +
            "\n" +
            "final class " + CLASS_NAME + SUFFIX + " {\n" +
            "  private " + CLASS_NAME + SUFFIX + "() {\n" +
            "  }\n" +
            "  public static void saveInstanceState(" + CLASS_NAME + " target, android.os.Bundle outState) {\n" +
            "    " + SAVEINSTANCESTATE + "\n" +
            "  }\n" +
            "  public static void restoreInstanceState(" + CLASS_NAME + " target, android.os.Bundle savedInstanceState) {\n" +
            "    if (savedInstanceState == null) {\n" +
            "      return;\n" +
            "    }\n" +
            "    " + RESTOREINSTANCESTATE + "\n" +
            "  }\n" +
            "}\n";

    private static final String COMMAND = "{command}";
    private static final String KEY = "{key}";
    private static final String NAME = "{name}";
    private static final String CAST = "{cast}";
    private static final String SAVEINSTANCESTATE_TEMPLATE = "outState.put" + COMMAND + "(\"" + KEY + "\", target." + NAME + ");";
    private static final String RESTOREINSTANCESTATE_TEMPLATE = "target." + NAME + " = " + CAST + "savedInstanceState.get" + COMMAND + "(\"" + KEY + "\");";
}
