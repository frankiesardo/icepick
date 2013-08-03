package com.github.frankiesardo.icepick.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@SupportedAnnotationTypes("com.github.frankiesardo.icepick.annotation.Icicle")
public class IcicleProcessor extends AbstractProcessor {

    public static final String SUFFIX = "$$Icicle";

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment env) {
        Set<? extends Element> elements = env.getElementsAnnotatedWith(Icicle.class);
        Map<TypeElement, Set<IcicleField>> fieldsByType = new HashMap<TypeElement, Set<IcicleField>>();
        Set<TypeMirror> erasedTargetTypes = new LinkedHashSet<TypeMirror>();
        groupFieldsByType(elements, fieldsByType, erasedTargetTypes);
        writeHelpers(fieldsByType, erasedTargetTypes);
        return true;
    }

    private void groupFieldsByType(Set<? extends Element> elements, Map<TypeElement, Set<IcicleField>> fieldsByType, Set<TypeMirror> erasedTargetTypes) {
        IcicleConverter icicleConverter = new IcicleConverter(new IcicleAssigner(processingEnv.getTypeUtils(), processingEnv.getElementUtils()));
        for (Element element : elements) {
            if (element.getModifiers().contains(Modifier.FINAL) ||
                    element.getModifiers().contains(Modifier.STATIC) ||
                    element.getModifiers().contains(Modifier.PRIVATE)) {
                error(element, "Field must not be private, static or final");
                continue;
            }
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            Set<IcicleField> fields = fieldsByType.get(enclosingElement);
            if (fields == null) {
                fields = new LinkedHashSet<IcicleField>();
                fieldsByType.put(enclosingElement, fields);
            }
            String fieldName = element.getSimpleName().toString();
            String fieldType = element.asType().toString();
            String fieldCommand = icicleConverter.convert(element.asType().toString());
            fields.add(new IcicleField(fieldName, fieldType, fieldCommand));

            // Add the type-erased version to the valid injection targets set.
            TypeMirror erasedTargetType = processingEnv.getTypeUtils().erasure(enclosingElement.asType());
            erasedTargetTypes.add(erasedTargetType);
        }
    }

    private void writeHelpers(Map<TypeElement, Set<IcicleField>> fieldsByType, Set<TypeMirror> erasedTargetTypes) {
        for (Map.Entry<TypeElement, Set<IcicleField>> entry : fieldsByType.entrySet()) {
            TypeElement classElement = entry.getKey();
            String parentFqcn = findParentFqcn(classElement, erasedTargetTypes);
            IcicleAssigner icicleAssigner = new IcicleAssigner(processingEnv.getTypeUtils(), processingEnv.getElementUtils());
            boolean isView = icicleAssigner.isAssignable(classElement.toString(), "android.view.View");

            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(classElement.getQualifiedName() + SUFFIX, classElement);
                Writer writer = jfo.openWriter();

                IcicleWriter icicleWriter = isView ? new IcicleViewWriter(writer, SUFFIX) : new IcicleFragmentActivityWriter(writer, SUFFIX);
                icicleWriter.writeClass(classElement, parentFqcn, entry.getValue());
            } catch (IOException e) {
                error(classElement, "Impossible to create %. Reason: %" + classElement.getQualifiedName() + SUFFIX, e);
            }
        }
    }

    private String findParentFqcn(TypeElement typeElement, Set<TypeMirror> parents) {
        TypeMirror type;
        while (true) {
            type = typeElement.getSuperclass();
            if (type.getKind() == TypeKind.NONE) {
                return null;
            }
            typeElement = (TypeElement) ((DeclaredType) type).asElement();
            if (containsTypeMirror(parents, type)) {
                return typeElement.getQualifiedName().toString();
            }
        }
    }

    private boolean containsTypeMirror(Collection<TypeMirror> mirrors, TypeMirror query) {
        // Ensure we are checking against a type-erased version for normalization purposes.
        Types typeUtils = processingEnv.getTypeUtils();
        query = typeUtils.erasure(query);

        for (TypeMirror mirror : mirrors) {
            if (typeUtils.isSameType(mirror, query)) {
                return true;
            }
        }
        return false;
    }

    private void error(Element element, String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(message, args), element);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
