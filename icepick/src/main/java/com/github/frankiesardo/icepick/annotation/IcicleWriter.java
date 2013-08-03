package com.github.frankiesardo.icepick.annotation;

import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

abstract class IcicleWriter {

    static final String BASE_KEY = "BASE_KEY";

    private final Writer writer;
    protected final String suffix;

    protected IcicleWriter(Writer writer, String suffix) {
        this.writer = writer;
        this.suffix = suffix;
    }

    void writeClass(TypeElement classType, Set<IcicleField> fields) throws IOException {
        String packageName = makePackage(classType);
        String className = makeType(classType);
        String saveInstanceStateBody = makeOnSaveInstanceStateBody(fields);
        String restoreInstanceStateBody = makeOnRestoreInstanceStateBody(fields);
        String saveInstanceStateStart = makeSaveInstanceStateStart(className, null);
        String restoreInstanceStateStart = makeRestoreInstanceStateStart(className);
        String saveInstanceStateEnd = makeSaveInstanceStateEnd();
        String restoreInstanceStateEnd = makeRestoreInstanceStateEnd(null);

        writeTemplateWith(packageName, className, saveInstanceStateStart, restoreInstanceStateStart, saveInstanceStateBody, restoreInstanceStateBody, saveInstanceStateEnd, restoreInstanceStateEnd);
    }

    private String makePackage(TypeElement classType) {
        return classType.getQualifiedName().toString().replace("." + classType.getSimpleName(), "");
    }

    private String makeType(TypeElement classType) {
        return classType.getSimpleName().toString();
    }

    private String makeOnRestoreInstanceStateBody(Set<IcicleField> fields) {
        StringBuilder builder = new StringBuilder();
        for (IcicleField field : fields) {
            builder.append(makeBundleGet(field));
        }
        return builder.toString();
    }

    private String makeBundleGet(IcicleField icicleField) {
        return "    target." + icicleField.getName() + " = " + icicleField.getTypeCast() + "savedInstanceState.get" + icicleField.getCommand() + "(" + BASE_KEY + " + \"" + icicleField.getName() + "\");\n";
    }

    private String makeOnSaveInstanceStateBody(Set<IcicleField> fields) {
        StringBuilder builder = new StringBuilder();
        for (IcicleField field : fields) {
            builder.append(makeBundlePut(field));
        }
        return builder.toString();
    }

    private String makeBundlePut(IcicleField icicleField) {
        return "    outState.put" + icicleField.getCommand() + "(" + BASE_KEY + " + \"" + icicleField.getName() + "\", target." + icicleField.getName() + ");\n";
    }

    protected abstract String makeSaveInstanceStateStart(String className, String parentFqcn);

    protected abstract String makeSaveInstanceStateEnd();

    protected abstract String makeRestoreInstanceStateStart(String className);

    protected abstract String makeRestoreInstanceStateEnd(String parentFqcn);

    private void writeTemplateWith(String packageName, String className, String saveInstanceStateStart, String restoreInstanceStateStart, String saveInstanceStateBody, String restoreInstanceStateBody, String saveInstanceStateEnd, String restoreInstanceStateEnd) throws IOException {
        String result = CLASS_TEMPLATE
                .replace(PACKAGE, packageName)
                .replace(CLASS_NAME, className)
                .replace(SUFFIX, suffix)
                .replace(SAVE_INSTANCE_STATE_START, saveInstanceStateStart)
                .replace(SAVE_INSTANCE_STATE_BODY, saveInstanceStateBody)
                .replace(RESTORE_INSTANCE_STATE_START, restoreInstanceStateStart)
                .replace(RESTORE_INSTANCE_STATE_BODY, restoreInstanceStateBody)
                .replace(SAVE_INSTANCE_STATE_END, saveInstanceStateEnd)
                .replace(RESTORE_INSTANCE_STATE_END, restoreInstanceStateEnd);
        writer.write(result);
        writer.flush();
        writer.close();
    }
    private static final String PACKAGE = "{packageName}";
    private static final String CLASS_NAME = "{className}";
    private static final String SUFFIX = "{suffix}";
    private static final String SAVE_INSTANCE_STATE_START = "{saveInstanceStateStart}";
    private static final String SAVE_INSTANCE_STATE_BODY = "{saveInstanceStateBody}";
    private static final String SAVE_INSTANCE_STATE_END = "{saveInstanceStateEnd}";
    private static final String RESTORE_INSTANCE_STATE_START = "{restoreInstanceStateStart}";
    private static final String RESTORE_INSTANCE_STATE_BODY = "{restoreInstanceStateBody}";

    private static final String RESTORE_INSTANCE_STATE_END = "{restoreInstanceStateEnd}";

    private static final String CLASS_TEMPLATE = "package " + PACKAGE + ";\n" +
            "\n" +
            "final class " + CLASS_NAME + SUFFIX + " {\n" +
            "\n" +
            "  private static final String BASE_KEY = \"" + PACKAGE + "." + CLASS_NAME + SUFFIX + ".\";\n" +
            "\n" +
            "  private " + CLASS_NAME + SUFFIX + "() {\n" +
            "  }\n" +
            "\n" +
            "" + SAVE_INSTANCE_STATE_START +
            "" + SAVE_INSTANCE_STATE_BODY +
            "" + SAVE_INSTANCE_STATE_END +
            "  }\n" +
            "\n" +
            "" + RESTORE_INSTANCE_STATE_START +
            "" + RESTORE_INSTANCE_STATE_BODY +
            "" + RESTORE_INSTANCE_STATE_END +
            "  }\n" +
            "}\n";
}
