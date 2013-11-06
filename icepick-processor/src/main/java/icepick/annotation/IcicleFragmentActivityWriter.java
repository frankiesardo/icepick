package icepick.annotation;

import javax.tools.JavaFileObject;

class IcicleFragmentActivityWriter extends IcicleWriter {

    IcicleFragmentActivityWriter(JavaFileObject jfo, String suffix) {
        super(jfo, suffix);
    }

    @Override
    protected String makeSaveInstanceStateStart(String className, String parentFqcn) {
        return "  public static void saveInstanceState(" + className + " target, android.os.Bundle outState) {\n" +
                makeSuperSaveCall(parentFqcn);
    }

    private String makeSuperSaveCall(String parentFqcn) {
        return parentFqcn != null ? "    " + parentFqcn + getSuffix() + ".saveInstanceState(target, outState);\n" : "";
    }

    @Override
    protected String makeRestoreInstanceStateStart(String className) {
        return "  public static void restoreInstanceState(" + className + " target, android.os.Bundle savedInstanceState) {\n" +
                "    if (savedInstanceState == null) {\n" +
                "      return;\n" +
                "    }\n";
    }

    @Override
    protected String makeRestoreInstanceStateEnd(String parentFqcn) {
        return parentFqcn != null ? "    " + parentFqcn + getSuffix() + ".restoreInstanceState(target, savedInstanceState);\n" : "";
    }

    @Override
    protected String makeSaveInstanceStateEnd() {
        return "";
    }
}
