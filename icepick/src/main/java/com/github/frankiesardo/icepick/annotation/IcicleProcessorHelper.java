package com.github.frankiesardo.icepick.annotation;

import javax.annotation.processing.Filer;
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
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

class IcicleProcessorHelper {
    private final Set<? extends Element> elements;
    private final Types typeUtils;
    private final Elements elementUtils;
    private final Filer filer;
    private final Messager messager;
    private final String suffix;

    public IcicleProcessorHelper(Set<? extends Element> elements, Types typeUtils, Elements elementUtils, Filer filer, Messager messager, String suffix) {
        this.elements = elements;
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.filer = filer;
        this.messager = messager;
        this.suffix = suffix;
    }

    public void process() {
        Map<TypeElement, Set<IcicleField>> fieldsByType = new HashMap<TypeElement, Set<IcicleField>>();
        Set<TypeMirror> erasedTargetTypes = new LinkedHashSet<TypeMirror>();
        groupFieldsByType(elements, fieldsByType, erasedTargetTypes);
        writeHelpers(fieldsByType, erasedTargetTypes);
    }

    private void groupFieldsByType(Set<? extends Element> elements, Map<TypeElement, Set<IcicleField>> fieldsByType, Set<TypeMirror> erasedTargetTypes) {
        IcicleConverter icicleConverter = new IcicleConverter(new IcicleAssigner(typeUtils, elementUtils));
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
            TypeMirror erasedTargetType = typeUtils.erasure(enclosingElement.asType());
            erasedTargetTypes.add(erasedTargetType);
        }
    }

    private void writeHelpers(Map<TypeElement, Set<IcicleField>> fieldsByType, Set<TypeMirror> erasedTargetTypes) {
        for (Map.Entry<TypeElement, Set<IcicleField>> entry : fieldsByType.entrySet()) {
            TypeElement classElement = entry.getKey();
            String parentFqcn = findParentFqcn(classElement, erasedTargetTypes);
            IcicleAssigner icicleAssigner = new IcicleAssigner(typeUtils, elementUtils);
            boolean isView = icicleAssigner.isAssignable(classElement.toString(), "android.view.View");

            try {
                JavaFileObject jfo = filer.createSourceFile(classElement.getQualifiedName() + suffix, classElement);
                Writer writer = jfo.openWriter();

                IcicleWriter icicleWriter = isView ? new IcicleViewWriter(writer, suffix) : new IcicleFragmentActivityWriter(writer, suffix);
                icicleWriter.writeClass(classElement, parentFqcn, entry.getValue());
            } catch (IOException e) {
                error(classElement, "Impossible to create %. Reason: %" + classElement.getQualifiedName() + suffix, e);
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
        // Ensure we are checking against a type-erased version for normalization purposes.;
        query = typeUtils.erasure(query);

        for (TypeMirror mirror : mirrors) {
            if (typeUtils.isSameType(mirror, query)) {
                return true;
            }
        }
        return false;
    }

    private void error(Element element, String message, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, args), element);
    }
}
