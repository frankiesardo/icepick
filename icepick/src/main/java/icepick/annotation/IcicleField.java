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

        private final Elements elementUtils;
        private final Types typeUtils;
        private final IcicleCommandConverter icicleCommandConverter;

        Factory(Elements elementUtils, Types typeUtils) {
            this.elementUtils = elementUtils;
            this.typeUtils = typeUtils;
            icicleCommandConverter = new IcicleCommandConverter(elementUtils, typeUtils);
        }

        public IcicleField from(Element element) {
            TypeMirror typeMirror = element.asType();
            String command = icicleCommandConverter.convert(typeMirror);
            String typeCast = IcicleCommand.TYPE_CAST_COMMANDS.contains(command) ? "(" + typeMirror.toString() + ")" : "";
            String name = element.getSimpleName().toString();
            return new IcicleField(name, typeCast, command);
        }
    }
}
