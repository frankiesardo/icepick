package icepick.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("icepick.annotation.Icicle")
public class IcicleProcessor extends AbstractProcessor {

    public static final String SUFFIX = "$$Icicle";

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment env) {
        IcicleFactory icicleFactory = IcicleFactory.from(processingEnv, SUFFIX);
        IcicleLogger icicleLogger = new IcicleLogger(processingEnv.getMessager());
        IcicleProcessorState icicleProcessorState = new IcicleProcessorState(processingEnv.getTypeUtils(), icicleFactory, icicleLogger);
        icicleProcessorState.process(env.getElementsAnnotatedWith(Icicle.class));
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}