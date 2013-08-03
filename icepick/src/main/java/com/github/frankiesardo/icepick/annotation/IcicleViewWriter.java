package com.github.frankiesardo.icepick.annotation;

import java.io.Writer;

class IcicleViewWriter extends IcicleWriter {

    static final String SUPER_SUFFIX = "\"$$SUPER$$\"";

    public IcicleViewWriter(Writer writer, String suffix) {
        super(writer, suffix);
    }

    @Override
    protected String makeSaveInstanceStateStart(String className) {
        return "  public static android.os.Parcelable saveInstanceState(" + className + " target, android.os.Parcelable state) {\n" +
                "    android.os.Bundle outState = new android.os.Bundle();\n" +
                "    outState.putParcelable(BASE_KEY + " + SUPER_SUFFIX + ", state);\n";
    }

    @Override
    protected String makeRestoreInstanceStateStart(String className) {
        return "  public static android.os.Parcelable restoreInstanceState(" + className + " target, android.os.Parcelable state) {\n" +
                "    android.os.Bundle savedInstanceState = (android.os.Bundle) state;\n";
    }

    @Override
    protected String makeRestoreInstanceStateEnd() {
        return "    return savedInstanceState.getParcelable(BASE_KEY + " + SUPER_SUFFIX + ");\n";
    }

    @Override
    protected String makeSaveInstanceStateEnd() {
        return "    return outState;\n";
    }
}
