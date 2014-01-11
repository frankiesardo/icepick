package icepick.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.Collection;

class EnvironmentFactory {

    private final FieldEnclosingClass.Factory enclosingClassFactory;
    private final AnnotatedField.Factory fieldFactory;
    private final ClassWriter.Factory writerFactory;

    static EnvironmentFactory from(ProcessingEnvironment processingEnv, String suffix) {
        Types typeUtils = processingEnv.getTypeUtils();
        Elements elementUtils = processingEnv.getElementUtils();
        FieldEnclosingClass.Factory enclosingClassFactory = new FieldEnclosingClass.Factory(typeUtils, elementUtils);
        AnnotatedField.Factory fieldFactory = new AnnotatedField.Factory(elementUtils, typeUtils);
        ClassWriter.Factory writerFactory = new ClassWriter.Factory(typeUtils, elementUtils, processingEnv.getFiler(), suffix);
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

    public ClassWriter makeWriter(FieldEnclosingClass enclosingClass) throws IOException {
        return writerFactory.from(enclosingClass);
    }
}
