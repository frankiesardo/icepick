package icepick.processor;

import javax.tools.JavaFileObject;

class ViewWriter extends AbsWriter {

  private static final String SUPER_SUFFIX = "\"$$SUPER$$\"";

  public ViewWriter(JavaFileObject jfo, String suffix, EnclosingClass enclosingClass) {
    super(jfo, suffix, enclosingClass);
  }

  @Override protected String getType() {
    return "Parcelable";
  }

  @Override protected String emitRestoreStateStart(EnclosingClass enclosingClass, String suffix) {
    return
        "  public Parcelable restoreInstanceState(Object obj, Parcelable state) {\n"
        + "    " + enclosingClass.getTargetClass() + " target = (" + enclosingClass.getTargetClass() + ") obj;\n"
        + "    Bundle savedInstanceState = (Bundle) state;\n"
        + "    Parcelable superState = savedInstanceState.getParcelable(" + BASE_KEY +
        " + " + SUPER_SUFFIX + ");\n";
  }

  @Override protected String emitRestoreStateEnd(EnclosingClass enclosingClass, String suffix) {
    String parentFqcn = enclosingClass.getParentEnclosingClass();
    return "    return " + (parentFqcn != null
        ? " new " + parentFqcn + suffix + "().restoreInstanceState(target, superState)"
        : "superState") + ";\n  }\n";
  }

  @Override protected String emitSaveStateStart(EnclosingClass enclosingClass, String suffix) {
    return "  public Parcelable saveInstanceState(Object obj, Parcelable state) {\n"
        + "    " + enclosingClass.getTargetClass() + " target = (" + enclosingClass.getTargetClass() + ") obj;\n"
        + "    Bundle outState = new Bundle();\n"
        + "    Parcelable superState = "
        + makeSaveSuperStateCall(enclosingClass.getParentEnclosingClass(), suffix)
        + ";\n"
        + "    outState.putParcelable(" + BASE_KEY + " + " + SUPER_SUFFIX + ", superState);\n";
  }

  private String makeSaveSuperStateCall(String parentFqcn, String suffix) {
    return parentFqcn != null ? "new " + parentFqcn + suffix + "().saveInstanceState(target, state)" : "state";
  }

  @Override protected String emitSaveStateEnd(EnclosingClass enclosingClass, String suffix) {
    return "    return outState;\n  }\n";
  }
}
