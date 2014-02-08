package icepick.processor;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Multimaps.index;

class AnnotationsConverter {

  private final Messager messager;
  private final Elements elementUtils;
  private final Types typeUtils;

  AnnotationsConverter(Messager messager, Elements elementUtils, Types typeUtils) {
    this.messager = messager;
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;
  }

  Map<EnclosingClass, Collection<AnnotatedField>> convert(
      Collection<? extends Element> annotatedElements) {
    FluentIterable<AnnotatedField> annotatedFields =
        from(annotatedElements).filter(new ValidModifier()).transform(new ToAnnotatedField());

    Set<TypeMirror> erasedEnclosingClasses =
        annotatedFields.transform(new ToErasedEnclosingClass()).toSet();

    return index(annotatedFields, new ByEnclosingClass(erasedEnclosingClasses)).asMap();
  }

  private class ValidModifier implements Predicate<Element> {
    @Override public boolean apply(Element element) {
      boolean isInvalid = element.getModifiers().contains(Modifier.PRIVATE) ||
          element.getModifiers().contains(Modifier.STATIC) ||
          element.getModifiers().contains(Modifier.FINAL);

      if (isInvalid) {
        logError(element, "Field must not be private, static or final");
      }

      return !isInvalid;
    }
  }

  private void logError(Element element, String error) {
    messager.printMessage(Diagnostic.Kind.ERROR, error, element);
  }

  private class ToAnnotatedField implements Function<Element, AnnotatedField> {

    final TypeMirror serializable = elementUtils.getTypeElement("java.io.Serializable").asType();
    final TypeMirror parcelable = elementUtils.getTypeElement("android.os.Parcelable").asType();

    @Override public AnnotatedField apply(Element element) {
      String name = element.getSimpleName().toString();
      TypeMirror type = element.asType();
      TypeElement enclosingClass = (TypeElement) element.getEnclosingElement();
      AnnotatedField.WrappingStrategy wrappingStrategy = wrappingStrategy(type);
      return new AnnotatedField(name, wrappingStrategy, type, enclosingClass);
    }

    private AnnotatedField.WrappingStrategy wrappingStrategy(TypeMirror type) {
      if (typeUtils.isAssignable(type, parcelable)) {
        return AnnotatedField.WrappingStrategy.PARCELABLE;
      }

      if (typeUtils.isAssignable(type, serializable)) {
        return AnnotatedField.WrappingStrategy.SERIALIZABLE;
      }

      if (isAnnotated(typeUtils.asElement(type), "org.parceler.Parcel")){
        return AnnotatedField.WrappingStrategy.PARCEL;
      }

      return AnnotatedField.WrappingStrategy.CUSTOM;
    }
  }

  private boolean isAnnotated(Element element, String annotationName) {
    if (element != null) {
      for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
        if (annotationMirror.getAnnotationType().asElement().toString().equals(annotationName)) {
          return true;
        }
      }
    }
    return false;
  }

  private class ToErasedEnclosingClass implements Function<AnnotatedField, TypeMirror> {
    @Override public TypeMirror apply(AnnotatedField field) {
      TypeElement enclosingClassType = field.getEnclosingClassType();
      if (enclosingClassType.getModifiers().contains(Modifier.PRIVATE)) {
        logError(enclosingClassType, "Enclosing class must not be private");
      }
      return typeUtils.erasure(enclosingClassType.asType());
    }
  }

  private class ByEnclosingClass implements Function<AnnotatedField, EnclosingClass> {

    private final Set<TypeMirror> erasedEnclosingClasses;

    private ByEnclosingClass(Set<TypeMirror> erasedEnclosingClasses) {
      this.erasedEnclosingClasses = erasedEnclosingClasses;
    }

    @Override public EnclosingClass apply(AnnotatedField field) {
      TypeElement classType = field.getEnclosingClassType();
      String classPackage = elementUtils.getPackageOf(classType).getQualifiedName().toString();
      int packageLength = classPackage.length() + 1;
      String targetClass = classType.getQualifiedName().toString().substring(packageLength);
      String className = targetClass.replace(".", "$");
      String parentFqcn = findParentFqcn(classType);
      return new EnclosingClass(classPackage, className, targetClass, parentFqcn, classType);
    }

    private String findParentFqcn(TypeElement classType) {
      TypeMirror type;
      while (true) {
        type = classType.getSuperclass();
        if (type.getKind() == TypeKind.NONE) {
          return null;
        }
        classType = (TypeElement) ((DeclaredType) type).asElement();
        if (containsTypeMirror(type)) {
          return classType.getQualifiedName().toString();
        }
      }
    }

    private boolean containsTypeMirror(TypeMirror query) {
      // Ensure we are checking against a type-erased version for normalization purposes.
      TypeMirror erasure = typeUtils.erasure(query);
      for (TypeMirror mirror : erasedEnclosingClasses) {
        if (typeUtils.isSameType(mirror, erasure)) {
          return true;
        }
      }
      return false;
    }
  }
}
