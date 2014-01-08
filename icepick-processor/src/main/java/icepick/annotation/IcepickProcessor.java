package icepick.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("icepick.annotation.Icepick")
public class IcepickProcessor extends AbstractProcessor {

    public static final String SUFFIX = "$$Icepick";

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment env) {
        EnvironmentFactory environmentFactory = EnvironmentFactory.from(processingEnv, SUFFIX);
        Logger logger = new Logger(processingEnv.getMessager());
        IcepickProcessorState icepickProcessorState = new IcepickProcessorState(processingEnv.getTypeUtils(), environmentFactory, logger);
        icepickProcessorState.process(env.getElementsAnnotatedWith(Icepick.class));
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}