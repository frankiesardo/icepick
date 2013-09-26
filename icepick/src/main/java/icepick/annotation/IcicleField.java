package icepick.annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class IcicleField {

    public final String name;
    public final String typeCast;
    public final String command;

    IcicleField(String name, String typeCast, String command) {
        this.name = name;
        this.command = command;
        this.typeCast = typeCast;
    }

    static class Factory {

        private final Types typeUtils;
        private final IcicleConversionMap conversionMap;

        Factory(Elements elementUtils, Types typeUtils) {
            this.typeUtils = typeUtils;
            this.conversionMap = new IcicleConversionMap(elementUtils, typeUtils);
        }

        public IcicleField from(Element element) {
            TypeMirror typeMirror = element.asType();
            String command = convert(typeMirror);
            String typeCast = IcicleConversionMap.TYPE_CAST_COMMANDS.contains(command) ? "(" + typeMirror.toString() + ")" : "";
            String name = element.getSimpleName().toString();
            return new IcicleField(name, typeCast, command);
        }

        private String convert(TypeMirror typeMirror) {
            for (TypeMirror other : conversionMap.keySet()) {
                if (typeUtils.isAssignable(typeMirror, other)) {
                    return conversionMap.get(other);
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
