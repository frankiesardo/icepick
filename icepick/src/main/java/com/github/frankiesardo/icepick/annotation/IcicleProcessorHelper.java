package com.github.frankiesardo.icepick.annotation;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
        IcicleConverter icicleConverter = new IcicleConverter(elementUtils, typeUtils);
        for (Element element : elements) {
            if (element.getModifiers().contains(Modifier.PRIVATE) ||
                    element.getModifiers().contains(Modifier.STATIC) ||
                    element.getModifiers().contains(Modifier.FINAL)) {
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
            String fieldCommand = icicleConverter.convert(element.asType());
            fields.add(new IcicleField(fieldName, fieldType, fieldCommand));

            // Add the type-erased version to the valid injection targets set.
            TypeMirror erasedTargetType = typeUtils.erasure(enclosingElement.asType());
            erasedTargetTypes.add(erasedTargetType);
        }
    }

    private void writeHelpers(Map<TypeElement, Set<IcicleField>> fieldsByType, Set<TypeMirror> erasedTargetTypes) {
        for (Map.Entry<TypeElement, Set<IcicleField>> entry : fieldsByType.entrySet()) {
            TypeElement classElement = entry.getKey();

            IcicleEnclosingClass icicleEnclosingClass = IcicleEnclosingClass.newInstance(classElement, erasedTargetTypes, typeUtils);

            boolean isView = typeUtils.isAssignable(classElement.asType(), elementUtils.getTypeElement("android.view.View").asType());

            try {
                JavaFileObject jfo = filer.createSourceFile(classElement.getQualifiedName() + suffix, classElement);
                Writer writer = jfo.openWriter();

                IcicleWriter icicleWriter = isView ? new IcicleViewWriter(writer, suffix) : new IcicleFragmentActivityWriter(writer, suffix);
                icicleWriter.writeClass(icicleEnclosingClass, entry.getValue());
            } catch (IOException e) {
                error(classElement, "Impossible to create %. Reason: %" + classElement.getQualifiedName() + suffix, e);
            }
        }
    }

    private void error(Element element, String message, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, args), element);
    }
}