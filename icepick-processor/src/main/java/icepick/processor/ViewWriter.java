package icepick.processor;

import javax.tools.JavaFileObject;

class ViewWriter extends AbsWriter {

  private static final String SUPER_SUFFIX = "\"$$SUPER$$\"";

  public ViewWriter(JavaFileObject jfo, String suffix, EnclosingClass enclosingClass) {
    super(jfo, suffix, enclosingClass);
  }

  @Override protected String emitRestoreStateStart(EnclosingClass enclosingClass, String suffix) {
    return
        "  public static android.os.Parcelable restoreInstanceState(" + enclosingClass.getClassName()
        + " target, Parcelable state) {\n"
        +
        "    Bundle savedInstanceState = (Bundle) state;\n"
        +
        "    Parcelable superState = savedInstanceState.getParcelable(" + BASE_KEY +
        " + " + SUPER_SUFFIX + ");\n";
  }

  @Override protected String emitRestoreStateEnd(EnclosingClass enclosingClass, String suffix) {
    String parentFqcn = enclosingClass.getParentEnclosingClass();
    return "    return " + (parentFqcn != null ?
        parentFqcn + suffix + ".restoreInstanceState(target, superState)"
        : "superState") + ";\n";
  }

  @Override protected String emitSaveStateStart(EnclosingClass enclosingClass, String suffix) {
    return "  public static Parcelable saveInstanceState("
        + enclosingClass.getClassName()
        + " target, Parcelable state) {\n"
        +
        "    Bundle outState = new Bundle();\n"
        +
        "    Parcelable superState = "
        + makeSaveSuperStateCall(enclosingClass.getParentEnclosingClass(), suffix)
        + ";\n" +
        "    outState.putParcelable(" + BASE_KEY + " + " + SUPER_SUFFIX + ", superState);\n";
  }

  private String makeSaveSuperStateCall(String parentFqcn, String suffix) {
    return parentFqcn != null ? parentFqcn + suffix + ".saveInstanceState(target, state)" : "state";
  }

  @Override protected String emitSaveStateEnd(EnclosingClass enclosingClass, String suffix) {
    return "    return outState;\n";
  }
}
