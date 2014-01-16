package icepick.processor;

import javax.lang.model.element.TypeElement;

class EnclosingClass {

  private final String classPackage;
  private final String className;
  private final String targetClass;
  private final String parentEnclosingClass;
  private final TypeElement element;

  EnclosingClass(String classPackage, String className, String targetClass,
      String parentEnclosingClass, TypeElement element) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
    this.parentEnclosingClass = parentEnclosingClass;
    this.element = element;
  }

  public String getClassPackage() {
    return classPackage;
  }

  public String getClassName() {
    return className;
  }

  public String getTargetClass() {
    return targetClass;
  }

  public String getParentEnclosingClass() {
    return parentEnclosingClass;
  }

  public TypeElement getElement() {
    return element;
  }
}
