package icepick.annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.*;

class IcicleProcessorState {

    private final Types typeUtils;
    private final EnvironmentFactory factory;
    private final Logger logger;

    public IcicleProcessorState(Types typeUtils, EnvironmentFactory factory, Logger logger) {
        this.typeUtils = typeUtils;
        this.factory = factory;
        this.logger = logger;
    }

    public void process(Set<? extends Element> elements) {
        Set<? extends Element> validElements = discardInvalid(elements);
        Map<TypeElement, Collection<Element>> elementsByEnclosingClass = groupElementsByEnclosingClass(validElements);
        Set<TypeMirror> erasedEnclosingClasses = findErasedEnclosingClasses(elementsByEnclosingClass.keySet());
        Map<FieldEnclosingClass, Collection<AnnotatedField>> convertedElements = convert(elementsByEnclosingClass, erasedEnclosingClasses);
        writeHelpers(convertedElements);
    }

    private Set<TypeMirror> findErasedEnclosingClasses(Set<TypeElement> enclosingClasses) {
        Set<TypeMirror> erasedEnclosingClasses = new HashSet<TypeMirror>(enclosingClasses.size());
        for (TypeElement classType : enclosingClasses) {
            erasedEnclosingClasses.add(typeUtils.erasure(classType.asType()));
        }
        return erasedEnclosingClasses;
    }

    private Set<? extends Element> discardInvalid(Set<? extends Element> elements) {
        Set<Element> validElements = new HashSet<Element>(elements.size());
        for (Element element : elements) {
            if (isValid(element)) {
                validElements.add(element);
            } else {
                logger.logError(element, "Field must not be private, static or final");
            }
        }
        return validElements;
    }

    private boolean isValid(Element element) {
        return !isInvalid(element);
    }

    private boolean isInvalid(Element element) {
        return element.getModifiers().contains(Modifier.PRIVATE) ||
                element.getModifiers().contains(Modifier.STATIC) ||
                element.getModifiers().contains(Modifier.FINAL);
    }

    private Map<TypeElement, Collection<Element>> groupElementsByEnclosingClass(Set<? extends Element> elements) {
        Map<TypeElement, Collection<Element>> elementsByEnclosingClass = new HashMap<TypeElement, Collection<Element>>();
        for (Element element : elements) {
            TypeElement enclosingClassType = (TypeElement) element.getEnclosingElement();
            Collection<Element> fields = elementsByEnclosingClass.get(enclosingClassType);
            if (fields == null) {
                fields = new LinkedHashSet<Element>();
                elementsByEnclosingClass.put(enclosingClassType, fields);
            }
            fields.add(element);
        }
        return elementsByEnclosingClass;
    }

    private Map<FieldEnclosingClass, Collection<AnnotatedField>> convert(Map<TypeElement, Collection<Element>> elementsByEnclosingClass, Set<TypeMirror> erasedEnclosingClasses) {
        Map<FieldEnclosingClass, Collection<AnnotatedField>> convertedElements = new HashMap<FieldEnclosingClass, Collection<AnnotatedField>>(elementsByEnclosingClass.size());
        for (TypeElement classType : elementsByEnclosingClass.keySet()) {
            FieldEnclosingClass fieldEnclosingClass = factory.makeEnclosingClass(classType, erasedEnclosingClasses);
            convertedElements.put(fieldEnclosingClass, convert(elementsByEnclosingClass.get(classType)));
        }
        return convertedElements;
    }

    private Collection<AnnotatedField> convert(Collection<Element> elements) {
        try {
            return attemptConversion(elements);
        } catch (AnnotatedField.UnableToSerializeException e) {
            logger.logError(e);
            throw e;
        }
    }

    private Collection<AnnotatedField> attemptConversion(Collection<Element> elements) {
        Set<AnnotatedField> convertedFields = new HashSet<AnnotatedField>(elements.size());
        for (Element e : elements) {
            convertedFields.add(factory.makeField(e));
        }
        return convertedFields;
    }

    private void writeHelpers(Map<FieldEnclosingClass, Collection<AnnotatedField>> elementsByEnclosingClass) {
        for (FieldEnclosingClass fieldEnclosingClass : elementsByEnclosingClass.keySet()) {
            try {
                ClassWriter classWriter = factory.makeWriter(fieldEnclosingClass.type);
                classWriter.writeClass(fieldEnclosingClass, elementsByEnclosingClass.get(fieldEnclosingClass));
            } catch (IOException e) {
                logger.logError("Impossible to create helper for %. Reason: %" + fieldEnclosingClass.className, e);
            }
        }
    }
}