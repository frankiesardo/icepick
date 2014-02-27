package icepick.processor;

import javax.tools.JavaFileObject;

class ObjectWriter extends AbsWriter {

  ObjectWriter(JavaFileObject jfo, String suffix, EnclosingClass enclosingClass) {
    super(jfo, suffix, enclosingClass);
  }

  @Override protected String getType() {
    return "Bundle";
  }

  @Override protected String emitRestoreStateStart(EnclosingClass enclosingClass, String suffix) {
    return "  public Bundle restoreInstanceState(Object obj, Bundle savedInstanceState) {\n"
        + "    " + enclosingClass.getTargetClass() + " target = (" + enclosingClass.getTargetClass() + ") obj;\n"
        + "    if (savedInstanceState == null) {\n"
        + "      return savedInstanceState;\n"
        + "    }\n";
  }

  @Override protected String emitRestoreStateEnd(EnclosingClass enclosingClass, String suffix) {
    String parentFqcn = enclosingClass.getParentEnclosingClass();
    return parentFqcn == null ? "    return savedInstanceState;\n  }\n" :
        "    return new " + parentFqcn + suffix + "().restoreInstanceState(target, savedInstanceState);\n  }\n";
  }

  @Override protected String emitSaveStateStart(EnclosingClass enclosingClass, String suffix) {
    return "  public Bundle saveInstanceState(Object obj, Bundle outState) {\n"
        + "    " + enclosingClass.getTargetClass() + " target = (" + enclosingClass.getTargetClass() + ") obj;\n"
        + makeSuperSaveCall(enclosingClass.getParentEnclosingClass(), suffix);
  }

  private String makeSuperSaveCall(String parentFqcn, String suffix) {
    return parentFqcn == null ? "" :
        "    new " + parentFqcn + suffix + "().saveInstanceState(target, outState);\n";
  }

  @Override protected String emitSaveStateEnd(EnclosingClass enclosingClass, String suffix) {
    return "    return outState;\n  }\n";
  }
}
