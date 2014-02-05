package icepick.processor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

class AnnotatedField {

  enum WrappingStrategy {
    SERIALIZABLE,
    PARCELABLE,
    CUSTOM
  }

  private final String name;
  private final WrappingStrategy wrappingStrategy;
  private final TypeMirror fieldType;
  private final TypeElement enclosingClassType;

  AnnotatedField(String name, WrappingStrategy wrappingStrategy, TypeMirror fieldType, TypeElement enclosingClassType) {
    this.name = name;
    this.wrappingStrategy = wrappingStrategy;
    this.fieldType = fieldType;
    this.enclosingClassType = enclosingClassType;
  }

  public String getName() {
    return name;
  }

  public WrappingStrategy getWrappingStrategy() {
    return wrappingStrategy;
  }

  public TypeMirror getFieldType() {
    return fieldType;
  }

  public TypeElement getEnclosingClassType() {
    return enclosingClassType;
  }
}
