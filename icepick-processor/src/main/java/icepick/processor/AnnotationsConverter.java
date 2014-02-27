package icepick.processor;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Messager;
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

    final TypeToMethodMap typeToMethodMap = new TypeToMethodMap(elementUtils, typeUtils);

    @Override public AnnotatedField apply(Element fieldElement) {
      String name = fieldElement.getSimpleName().toString();
      TypeMirror fieldType = fieldElement.asType();
      TypeElement enclosingClass = (TypeElement) fieldElement.getEnclosingElement();
      String bundleMethod = typeToMethodMap.convert(fieldType);
      if (bundleMethod == null) {
        logError(fieldElement, "Don't know how to put a " + fieldType + " inside a Bundle");
      }
      String typeCast = typeToMethodMap.requiresTypeCast(bundleMethod) ? "(" + fieldType + ")" : "";

      return new AnnotatedField(name, bundleMethod, typeCast, enclosingClass);
    }
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
      String classPackage = getPackageName(classType);
      String targetClass = getClassName(classType, classPackage);
      String sanitizedClassName = sanitize(targetClass);
      String parentFqcn = findParentFqcn(classType);
      return new EnclosingClass(classPackage, sanitizedClassName, targetClass, parentFqcn, classType);
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
          String packageName = getPackageName(classType);
          return packageName + "." + sanitize(getClassName(classType, packageName));
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

    private String getPackageName(TypeElement classType) {
      return elementUtils.getPackageOf(classType).getQualifiedName().toString();
    }

    private String getClassName(TypeElement classType, String classPackage) {
      int packageLength = classPackage.length() + 1;
      return classType.getQualifiedName().toString().substring(packageLength);
    }

    private String sanitize(String targetClass) {
      return targetClass.replace(".", "$");
    }
  }
}
