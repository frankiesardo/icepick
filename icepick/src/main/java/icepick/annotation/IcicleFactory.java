package icepick.annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.Collection;

class IcicleFactory {

    private final IcicleEnclosingClass.Factory enclosingClassFactory;
    private final IcicleField.Factory fieldFactory;
    private final IcicleWriter.Factory writerFactory;

    static IcicleFactory from(ProcessingEnvironment processingEnv, String suffix) {
        IcicleEnclosingClass.Factory enclosingClassFactory = new IcicleEnclosingClass.Factory(processingEnv.getTypeUtils());
        IcicleField.Factory fieldFactory = new IcicleField.Factory(processingEnv.getElementUtils(), processingEnv.getTypeUtils());
        IcicleWriter.Factory writerFactory = new IcicleWriter.Factory(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getFiler(), suffix);
        return new IcicleFactory(enclosingClassFactory, fieldFactory, writerFactory);
    }

    IcicleFactory(IcicleEnclosingClass.Factory enclosingClassFactory, IcicleField.Factory FieldFactory, IcicleWriter.Factory writerFactory) {
        this.enclosingClassFactory = enclosingClassFactory;
        this.fieldFactory = FieldFactory;
        this.writerFactory = writerFactory;
    }

    public IcicleEnclosingClass makeEnclosingClass(TypeElement classType, Collection<TypeMirror> parents) {
        return enclosingClassFactory.from(classType, parents);
    }

    public IcicleField makeField(Element element) {
        return fieldFactory.from(element);
    }

    public IcicleWriter makeWriter(TypeElement classType) throws IOException {
        return writerFactory.from(classType);
    }
}
