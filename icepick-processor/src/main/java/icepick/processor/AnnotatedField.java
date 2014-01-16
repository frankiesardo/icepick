package icepick.processor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

class AnnotatedField {

    private final String name;
    private final TypeMirror fieldType;
    private final TypeElement enclosingClassType;

    AnnotatedField(String name, TypeMirror fieldType, TypeElement enclosingClassType) {
        this.name = name;
        this.fieldType = fieldType;
        this.enclosingClassType = enclosingClassType;
    }

  public String getName() {
    return name;
  }

  public TypeMirror getFieldType() {
    return fieldType;
  }

  public TypeElement getEnclosingClassType() {
    return enclosingClassType;
  }
}
