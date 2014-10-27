package icepick.processor;

import javax.lang.model.element.TypeElement;

class AnnotatedField {

  private final String name;
  private final String bundleMethod;
  private final String typeCast;
  private final TypeElement enclosingClassType;
  private final boolean nullable;

  AnnotatedField(String name, String bundleMethod, String typeCast, TypeElement enclosingClassType, boolean nullable) {
    this.name = name;
    this.bundleMethod = bundleMethod;
    this.typeCast = typeCast;
    this.enclosingClassType = enclosingClassType;
    this.nullable = nullable;
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

  public boolean isNullable() {
        return nullable;
    }
}
