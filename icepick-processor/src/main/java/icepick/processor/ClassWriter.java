package icepick.processor;

import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

class ClassWriter {
  private final Elements elementUtils;
  private final Types typeUtils;
  private final Filer filer;
  private final String suffix;

  public ClassWriter(Elements elementUtils, Types typeUtils, Filer filer, String suffix) {
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;
    this.filer = filer;
    this.suffix = suffix;
  }

  public AbsWriter writeClass(EnclosingClass enclosingClass) throws IOException {
    TypeElement classType = enclosingClass.getElement();
    String fqcn = enclosingClass.getClassPackage() + "." + enclosingClass.getClassName() + suffix;
    JavaFileObject jfo = filer.createSourceFile(fqcn, classType);
    boolean isView = typeUtils.isAssignable(classType.asType(),
        elementUtils.getTypeElement("android.view.View").asType());
    return isView ? new ViewWriter(jfo, suffix, enclosingClass) : new GenericWriter(jfo, suffix, enclosingClass);
  }
}
