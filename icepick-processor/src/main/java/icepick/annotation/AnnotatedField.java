package icepick.annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class AnnotatedField {

    public final String name;
    public final String typeCast;
    public final String command;

    AnnotatedField(String name, String typeCast, String command) {
        this.name = name;
        this.command = command;
        this.typeCast = typeCast;
    }

    static class Factory {

        private final Types typeUtils;
        private final FieldConversionMap fieldConversionMap;

        Factory(Elements elementUtils, Types typeUtils) {
            this.typeUtils = typeUtils;
            this.fieldConversionMap = new FieldConversionMap(elementUtils, typeUtils);
        }

        public AnnotatedField from(Element element) {
            TypeMirror typeMirror = element.asType();
            String command = convert(typeMirror);
            String typeCast = FieldConversionMap.TYPE_CAST_COMMANDS.contains(command) ? "(" + typeMirror.toString() + ")" : "";
            String name = element.getSimpleName().toString();
            return new AnnotatedField(name, typeCast, command);
        }

        private String convert(TypeMirror typeMirror) {
            for (TypeMirror other : fieldConversionMap.keySet()) {
                if (typeUtils.isAssignable(typeMirror, other)) {
                    return fieldConversionMap.get(other);
                }
            }
            throw new UnableToSerializeException(typeMirror);
        }
    }

    public static class UnableToSerializeException extends RuntimeException {
        UnableToSerializeException(TypeMirror fieldType) {
            super("Don't know how to put a " + fieldType + " into a Bundle");
        }
    }
}
