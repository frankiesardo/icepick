package icepick.processor;

import javax.tools.JavaFileObject;

class GenericWriter extends AbsWriter {

    GenericWriter(JavaFileObject jfo, String suffix, EnclosingClass enclosingClass) {
        super(jfo, suffix, enclosingClass);
    }

  @Override protected String emitRestoreStateStart(EnclosingClass enclosingClass, String suffix) {
    return "  public static void restoreInstanceState("
        + enclosingClass.getTargetClass()
        + " target, Bundle savedInstanceState) {\n"
        + "    if (savedInstanceState == null) {\n"
        + "      return;\n"
        + "    }\n";
  }

  @Override protected String emitRestoreStateEnd(EnclosingClass enclosingClass, String suffix) {
    String parentFqcn = enclosingClass.getParentEnclosingClass();
    return parentFqcn == null ? "  }\n" :
        "    " + parentFqcn + suffix + ".restoreInstanceState(target, savedInstanceState);\n  }\n";
  }

  @Override protected String emitSaveStateStart(EnclosingClass enclosingClass, String suffix) {
    return "  public static void saveInstanceState("
        + enclosingClass.getTargetClass()
        + " target, Bundle outState) {\n"
        + makeSuperSaveCall(enclosingClass.getParentEnclosingClass(), suffix);
  }

  private String makeSuperSaveCall(String parentFqcn, String suffix) {
    return parentFqcn == null ? "" :
        "    " + parentFqcn + suffix + ".saveInstanceState(target, outState);\n";
  }

  @Override protected String emitSaveStateEnd(EnclosingClass enclosingClass, String suffix) {
    return "  }\n";
  }
}
