package icepick.annotation;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

abstract class ClassWriter {

    static final String BASE_KEY = "BASE_KEY";

    private final JavaFileObject javaFileObject;
    private final String suffix;

    protected ClassWriter(JavaFileObject javaFileObject, String suffix) {
        this.javaFileObject = javaFileObject;
        this.suffix = suffix;
    }

    protected String getSuffix() {
        return suffix;
    }

    void writeClass(FieldEnclosingClass fieldEnclosingClass, Collection<AnnotatedField> fields) throws IOException {
        Writer writer = javaFileObject.openWriter();
        writer.write(getClassTemplate(fieldEnclosingClass, fields));
        writer.flush();
        writer.close();
    }

    private String getClassTemplate(FieldEnclosingClass fieldEnclosingClass, Collection<AnnotatedField> fields) throws IOException {
        String className = fieldEnclosingClass.className;
        String packageName = fieldEnclosingClass.packageName;
        String saveInstanceStateBody = makeOnSaveInstanceStateBody(fields);
        String restoreInstanceStateBody = makeOnRestoreInstanceStateBody(fields);
        String saveInstanceStateStart = makeSaveInstanceStateStart(className, fieldEnclosingClass.parentFqcn);
        String restoreInstanceStateStart = makeRestoreInstanceStateStart(className);
        String saveInstanceStateEnd = makeSaveInstanceStateEnd();
        String restoreInstanceStateEnd = makeRestoreInstanceStateEnd(fieldEnclosingClass.parentFqcn);

        return replaceTemplateWith(packageName, className, saveInstanceStateStart, restoreInstanceStateStart, saveInstanceStateBody, restoreInstanceStateBody, saveInstanceStateEnd, restoreInstanceStateEnd);
    }

    private String makeOnRestoreInstanceStateBody(Collection<AnnotatedField> fields) {
        StringBuilder builder = new StringBuilder();
        for (AnnotatedField field : fields) {
            builder.append(makeBundleGet(field));
        }
        return builder.toString();
    }

    private String makeBundleGet(AnnotatedField annotatedField) {
        return "    target." + annotatedField.name + " = " + annotatedField.typeCast + "savedInstanceState.get" + annotatedField.command + "(" + BASE_KEY + " + \"" + annotatedField.name + "\");\n";
    }

    private String makeOnSaveInstanceStateBody(Collection<AnnotatedField> fields) {
        StringBuilder builder = new StringBuilder();
        for (AnnotatedField field : fields) {
            builder.append(makeBundlePut(field));
        }
        return builder.toString();
    }

    private String makeBundlePut(AnnotatedField annotatedField) {
        return "    outState.put" + annotatedField.command + "(" + BASE_KEY + " + \"" + annotatedField.name + "\", target." + annotatedField.name + ");\n";
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
            "public final class " + CLASS_NAME + SUFFIX + " {\n" +
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

        public ClassWriter from(TypeElement classType) throws IOException {
            JavaFileObject jfo = filer.createSourceFile(classType.getQualifiedName() + suffix, classType);
            boolean isView = typeUtils.isAssignable(classType.asType(), elementUtils.getTypeElement("android.view.View").asType());
            return isView ? new ViewWriter(jfo, suffix) : new FragmentActivityWriter(jfo, suffix);
        }
    }
}
