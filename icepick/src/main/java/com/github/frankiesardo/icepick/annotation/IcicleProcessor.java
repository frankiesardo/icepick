package com.github.frankiesardo.icepick.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("com.github.frankiesardo.icepick.annotation.Icicle")
public class IcicleProcessor extends AbstractProcessor {

    public static final String SUFFIX = "$$Icicle";

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment env) {
        Set<? extends Element> elements = env.getElementsAnnotatedWith(Icicle.class);
        Map<TypeElement, Set<IcicleField>> fieldsByType = new HashMap<TypeElement, Set<IcicleField>>();
        groupFieldsByType(elements, fieldsByType);
        writeHelpers(fieldsByType);
        return true;
    }

    private void groupFieldsByType(Set<? extends Element> elements, Map<TypeElement, Set<IcicleField>> fieldsByType) {
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
        }
    }

    private void writeHelpers(Map<TypeElement, Set<IcicleField>> fieldsByType) {
        for (Map.Entry<TypeElement, Set<IcicleField>> entry : fieldsByType.entrySet()) {
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(entry.getKey().getQualifiedName() + SUFFIX, entry.getKey());
                Writer writer = jfo.openWriter();

                IcicleViewWriter icicleWriter = new IcicleViewWriter(writer, SUFFIX);
                icicleWriter.writeClass(entry.getKey(), entry.getValue());
            } catch (IOException e) {
                error(entry.getKey(), "Impossible to create %. Reason: %" + entry.getKey().getQualifiedName() + SUFFIX, e);
            }
        }
    }

    private void error(Element element, String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(message, args), element);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
