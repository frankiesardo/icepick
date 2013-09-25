package icepick.annotation;

import java.io.Writer;

class IcicleViewWriter extends IcicleWriter {

    static final String SUPER_SUFFIX = "\"$$SUPER$$\"";

    public IcicleViewWriter(Writer writer, String suffix) {
        super(writer, suffix);
    }

    @Override
    protected String makeSaveInstanceStateStart(String className, String parentFqcn) {
        return "  public static android.os.Parcelable saveInstanceState(" + className + " target, android.os.Parcelable state) {\n" +
                "    android.os.Bundle outState = new android.os.Bundle();\n" +
                "    android.os.Parcelable superState = " + makeSaveSuperStateCall(parentFqcn) + ";\n" +
                "    outState.putParcelable(" + BASE_KEY + " + " + SUPER_SUFFIX + ", superState);\n";
    }

    private String makeSaveSuperStateCall(String parentFqcn) {
        return parentFqcn != null ? parentFqcn + getSuffix() + ".saveInstanceState(target, state)" : "state";
    }

    @Override
    protected String makeSaveInstanceStateEnd() {
        return "    return outState;\n";
    }

    @Override
    protected String makeRestoreInstanceStateStart(String className) {
        return "  public static android.os.Parcelable restoreInstanceState(" + className + " target, android.os.Parcelable state) {\n" +
                "    android.os.Bundle savedInstanceState = (android.os.Bundle) state;\n" +
                "    android.os.Parcelable superState = savedInstanceState.getParcelable(" + BASE_KEY + " + " + SUPER_SUFFIX + ");\n";
    }

    @Override
    protected String makeRestoreInstanceStateEnd(String parentFqcn) {
        return "    return " + (parentFqcn != null ? parentFqcn + getSuffix() + ".restoreInstanceState(target, superState)" : "superState") + ";\n";
    }
}
