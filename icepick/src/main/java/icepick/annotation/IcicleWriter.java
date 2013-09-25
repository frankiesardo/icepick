package icepick.annotation;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

abstract class IcicleWriter {

    static final String BASE_KEY = "BASE_KEY";

    private final JavaFileObject javaFileObject;
    private final String suffix;

    protected IcicleWriter(JavaFileObject javaFileObject, String suffix) {
        this.javaFileObject = javaFileObject;
        this.suffix = suffix;
    }

    protected String getSuffix() {
        return suffix;
    }

    void writeClass(IcicleEnclosingClass enclosingClass, Collection<IcicleField> fields) throws IOException {
        Writer writer = javaFileObject.openWriter();
        writer.write(getClassTemplate(enclosingClass, fields));
        writer.flush();
        writer.close();
    }

    private String getClassTemplate(IcicleEnclosingClass enclosingClass, Collection<IcicleField> fields) throws IOException {
        String className = enclosingClass.className;
        String packageName = enclosingClass.packageName;
        String saveInstanceStateBody = makeOnSaveInstanceStateBody(fields);
        String restoreInstanceStateBody = makeOnRestoreInstanceStateBody(fields);
        String saveInstanceStateStart = makeSaveInstanceStateStart(className, enclosingClass.parentFqcn);
        String restoreInstanceStateStart = makeRestoreInstanceStateStart(className);
        String saveInstanceStateEnd = makeSaveInstanceStateEnd();
        String restoreInstanceStateEnd = makeRestoreInstanceStateEnd(enclosingClass.parentFqcn);

        return replaceTemplateWith(packageName, className, saveInstanceStateStart, restoreInstanceStateStart, saveInstanceStateBody, restoreInstanceStateBody, saveInstanceStateEnd, restoreInstanceStateEnd);
    }

    private String makeOnRestoreInstanceStateBody(Collection<IcicleField> fields) {
        StringBuilder builder = new StringBuilder();
        for (IcicleField field : fields) {
            builder.append(makeBundleGet(field));
        }
        return builder.toString();
    }

    private String makeBundleGet(IcicleField icicleField) {
        return "    target." + icicleField.name + " = " + icicleField.typeCast + "savedInstanceState.get" + icicleField.command + "(" + BASE_KEY + " + \"" + icicleField.name + "\");\n";
    }

    private String makeOnSaveInstanceStateBody(Collection<IcicleField> fields) {
        StringBuilder builder = new StringBuilder();
        for (IcicleField field : fields) {
            builder.append(makeBundlePut(field));
        }
        return builder.toString();
    }

    private String makeBundlePut(IcicleField icicleField) {
        return "    outState.put" + icicleField.command + "(" + BASE_KEY + " + \"" + icicleField.name + "\", target." + icicleField.name + ");\n";
    }

    protected abstract String makeSaveInstanceStateStart(String className, String parentFqcn);

    protected abstract String makeSaveInstanceStateEnd();

    protected abstract String makeRestoreInstanceStateStart(String className);

    protected abstract String makeRestoreInstanceStateEnd(String parentFqcn);

    private String replaceTemplateWith(String packageName, String className, String saveInstanceStateStart, String restoreInstanceStateStart, String saveInstanceStateBody, String restoreInstanceStateBody, String saveInstanceStateEnd, String restoreInstanceStateEnd) throws IOException {
        return CLASS_TEMPLATE
                .replace(PACKAGE, packageName)
                .replace(CLASS_NAME, className)
                .replace(SUFFIX, getSuffix())
                .replace(SAVE_INSTANCE_STATE_START, saveInstanceStateStart)
                .replace(SAVE_INSTANCE_STATE_BODY, saveInstanceStateBody)
                .replace(RESTORE_INSTANCE_STATE_START, restoreInstanceStateStart)
                .replace(RESTORE_INSTANCE_STATE_BODY, restoreInstanceStateBody)
                .replace(SAVE_INSTANCE_STATE_END, saveInstanceStateEnd)
                .replace(RESTORE_INSTANCE_STATE_END, restoreInstanceStateEnd);
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

    static class Factory {

        private final Types typeUtils;
        private final Elements elementUtils;
        private final Filer filer;
        private final String suffix;

        Factory(Types typeUtils, Elements elementUtils, Filer filer, String suffix) {
            this.typeUtils = typeUtils;
            this.elementUtils = elementUtils;
            this.filer = filer;
            this.suffix = suffix;
        }

        public IcicleWriter from(TypeElement classType) throws IOException {
            JavaFileObject jfo = filer.createSourceFile(classType.getQualifiedName() + suffix, classType);
            boolean isView = typeUtils.isAssignable(classType.asType(), elementUtils.getTypeElement("android.view.View").asType());
            return isView ? new IcicleViewWriter(jfo, suffix) : new IcicleFragmentActivityWriter(jfo, suffix);
        }
    }
}
