package icepick.annotation;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

class IcicleFactory {

    private final Types typeUtils;
    private final Elements elementUtils;
    private final Filer filer;
    private final String suffix;

    IcicleFactory(Types typeUtils, Elements elementUtils, Filer filer, String suffix) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.filer = filer;
        this.suffix = suffix;
    }

    public IcicleWriter makeWriter(TypeElement classType) throws IOException {
        JavaFileObject jfo = filer.createSourceFile(classType.getQualifiedName() + suffix, classType);
        Writer writer = jfo.openWriter();
        boolean isView = typeUtils.isAssignable(classType.asType(), elementUtils.getTypeElement("android.view.View").asType());
        return isView ? new IcicleViewWriter(writer, suffix) : new IcicleFragmentActivityWriter(writer, suffix);
    }

    public IcicleEnclosingClass makeEnclosingClass(TypeElement classType, Collection<TypeMirror> parents) {
        return IcicleEnclosingClass.newInstance(classType, parents, typeUtils);
    }

    public IcicleField makeField(Element element) {
        String command = new IcicleCommandConverter(elementUtils, typeUtils).convert(element.asType());
        return new IcicleField(element.getSimpleName().toString(), element.asType().toString(), command);
    }
}
