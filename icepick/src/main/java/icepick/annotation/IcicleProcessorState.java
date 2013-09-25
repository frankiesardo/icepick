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
    private final IcicleFactory factory;
    private final IcicleLogger icicleLogger;

    public IcicleProcessorState(Types typeUtils, IcicleFactory factory, IcicleLogger icicleLogger) {
        this.typeUtils = typeUtils;
        this.factory = factory;
        this.icicleLogger = icicleLogger;
    }

    public void process(Set<? extends Element> elements) {
        Set<? extends Element> validElements = discardInvalid(elements);
        Map<TypeElement, Collection<Element>> elementsByEnclosingClass = groupElementsByEnclosingClass(validElements);
        Set<TypeMirror> erasedEnclosingClasses = findErasedEnclosingClasses(elementsByEnclosingClass.keySet());
        Map<IcicleEnclosingClass, Collection<IcicleField>> convertedElements = convert(elementsByEnclosingClass, erasedEnclosingClasses);
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
                icicleLogger.logError(element, "Field must not be private, static or final");
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

    private Map<IcicleEnclosingClass, Collection<IcicleField>> convert(Map<TypeElement, Collection<Element>> elementsByEnclosingClass, Set<TypeMirror> erasedEnclosingClasses) {
        Map<IcicleEnclosingClass, Collection<IcicleField>> convertedElements = new HashMap<IcicleEnclosingClass, Collection<IcicleField>>(elementsByEnclosingClass.size());
        for (TypeElement classType : elementsByEnclosingClass.keySet()) {
            IcicleEnclosingClass enclosingClass = factory.makeEnclosingClass(classType, erasedEnclosingClasses);
            convertedElements.put(enclosingClass, convert(elementsByEnclosingClass.get(classType)));
        }
        return convertedElements;
    }

    private Collection<IcicleField> convert(Collection<Element> elements) {
        Set<IcicleField> convertedFields = new HashSet<IcicleField>(elements.size());
        for (Element e : elements) {
            convertedFields.add(factory.makeField(e));
        }
        return convertedFields;
    }

    private void writeHelpers(Map<IcicleEnclosingClass, Collection<IcicleField>> elementsByEnclosingClass) {
        for (IcicleEnclosingClass enclosingClass : elementsByEnclosingClass.keySet()) {
            try {
                IcicleWriter icicleWriter = factory.makeWriter(enclosingClass.type);
                icicleWriter.writeClass(enclosingClass, elementsByEnclosingClass.get(enclosingClass));
            } catch (IOException e) {
                icicleLogger.logError("Impossible to create helper for %. Reason: %" + enclosingClass.className, e);
            }
        }
    }
}