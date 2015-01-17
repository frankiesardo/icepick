package icepick.processor;

import javax.lang.model.element.TypeElement;

class AnnotatedField {

  private final String name;
  private final String bundleMethod;
  private final String typeCast;
  private final TypeElement enclosingClassType;
  private final boolean isPrimitive;

    AnnotatedField(String name, String bundleMethod, String typeCast, TypeElement enclosingClassType, boolean isPrimitive) {
    this.name = name;
    this.bundleMethod = bundleMethod;
    this.typeCast = typeCast;
    this.enclosingClassType = enclosingClassType;
    this.isPrimitive = isPrimitive;
  }

  public String getName() {
    return name;
  }

  public String getBundleMethod() {
    return bundleMethod;
  }

  public String getTypeCast() {
    return typeCast;
  }

  public TypeElement getEnclosingClassType() {
    return enclosingClassType;
  }

  public boolean isPrimitive() {
    return isPrimitive;
  }
}
