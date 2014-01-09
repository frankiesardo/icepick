package icepick.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.Collection;

class EnvironmentFactory {

    private final FieldEnclosingClass.Factory enclosingClassFactory;
    private final AnnotatedField.Factory fieldFactory;
    private final ClassWriter.Factory writerFactory;

    static EnvironmentFactory from(ProcessingEnvironment processingEnv, String suffix) {
        FieldEnclosingClass.Factory enclosingClassFactory = new FieldEnclosingClass.Factory(processingEnv.getTypeUtils());
        AnnotatedField.Factory fieldFactory = new AnnotatedField.Factory(processingEnv.getElementUtils(), processingEnv.getTypeUtils());
        ClassWriter.Factory writerFactory = new ClassWriter.Factory(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getFiler(), suffix);
        return new EnvironmentFactory(enclosingClassFactory, fieldFactory, writerFactory);
    }

    EnvironmentFactory(FieldEnclosingClass.Factory enclosingClassFactory, AnnotatedField.Factory FieldFactory, ClassWriter.Factory writerFactory) {
        this.enclosingClassFactory = enclosingClassFactory;
        this.fieldFactory = FieldFactory;
        this.writerFactory = writerFactory;
    }

    public FieldEnclosingClass makeEnclosingClass(TypeElement classType, Collection<TypeMirror> parents) {
        return enclosingClassFactory.from(classType, parents);
    }

    public AnnotatedField makeField(Element element) {
        return fieldFactory.from(element);
    }

    public ClassWriter makeWriter(TypeElement classType) throws IOException {
        return writerFactory.from(classType);
    }
}
