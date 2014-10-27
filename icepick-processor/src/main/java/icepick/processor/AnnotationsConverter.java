package icepick.processor;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import icepick.Icicle;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
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

    Set<String> erasedEnclosingClasses =
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

    final TypeToBundleMethodMap
        typeToBundleMethodMap = new TypeToBundleMethodMap(elementUtils, typeUtils);
    final NullSafetyDetector
            nullSafetyDetector = new NullSafetyDetector(typeUtils);

    @Override public AnnotatedField apply(Element fieldElement) {
      String name = fieldElement.getSimpleName().toString();
      TypeMirror fieldType = fieldElement.asType();
      TypeElement enclosingClass = (TypeElement) fieldElement.getEnclosingElement();
      String bundleMethod = typeToBundleMethodMap.convert(fieldType);
      if (bundleMethod == null) {
        logError(fieldElement, "Don't know how to put a " + fieldType + " inside a Bundle");
      }
      String typeCast = TypecastStrategy.resolve(bundleMethod, fieldType);
      boolean nullability = nullSafetyDetector.isNullable(fieldElement);

      return new AnnotatedField(name, bundleMethod, typeCast, enclosingClass, nullability);
    }
  }

  private class ToErasedEnclosingClass implements Function<AnnotatedField, String> {
    @Override public String apply(AnnotatedField field) {
      TypeElement enclosingClassType = field.getEnclosingClassType();
      if (enclosingClassType.getModifiers().contains(Modifier.PRIVATE)) {
        logError(enclosingClassType, "Enclosing class must not be private");
      }
      return enclosingClassType.toString();
    }
  }

  private class ByEnclosingClass implements Function<AnnotatedField, EnclosingClass> {

    private final Set<String> ignoredClasses = new HashSet<String>();
    private final Set<String> annotatedClasses = new HashSet<String>();

    private ByEnclosingClass(Set<String> erasedEnclosingClasses) {
      annotatedClasses.addAll(erasedEnclosingClasses);
    }

    @Override public EnclosingClass apply(AnnotatedField field) {
      TypeElement classType = field.getEnclosingClassType();
      String classPackage = getPackageName(classType);
      String targetClassName = getClassName(classType, classPackage);
      String sanitizedClassName = sanitize(targetClassName);
      String parentFqcn = findParentFqcn(classType);
      return new EnclosingClass(classPackage, sanitizedClassName, targetClassName, parentFqcn, classType);
    }

    private String findParentFqcn(TypeElement classType) {
      TypeMirror type;
      while (true) {
        type = classType.getSuperclass();
        if (type.getKind() == TypeKind.NONE) {
          return null;
        }
        classType = (TypeElement) ((DeclaredType) type).asElement();
        String erasedClassName = classType.toString();

        if (ignoredClasses.contains(erasedClassName)) {
          continue;
        }

        if (annotatedClasses.contains(erasedClassName)) {
          return getFqcn(classType);
        }

        if (isAnnotatedFromAnotherSourceSet(classType)) {
          annotatedClasses.add(erasedClassName);
          return getFqcn(classType);
        }

        ignoredClasses.add(erasedClassName);
      }
    }

    private boolean isAnnotatedFromAnotherSourceSet(TypeElement query) {
      List<VariableElement> fields = ElementFilter.fieldsIn(query.getEnclosedElements());

      for (Element e : fields) {
        for (AnnotationMirror am : e.getAnnotationMirrors()) {
          if (am.getAnnotationType().asElement().toString().equals(Icicle.class.getName())) {
            return true;
          }
        }
      }
      return false;
    }

    private String getFqcn(TypeElement classType) {
      String packageName = getPackageName(classType);
      return packageName + "." + sanitize(getClassName(classType, packageName));
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
