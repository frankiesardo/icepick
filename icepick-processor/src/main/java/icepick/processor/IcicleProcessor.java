package icepick.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("icepick.Icicle")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class IcicleProcessor extends AbstractProcessor {

    public static final String SUFFIX = "$$Icicle";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        EnvironmentFactory environmentFactory = EnvironmentFactory.from(processingEnv, SUFFIX);
        Logger logger = new Logger(processingEnv.getMessager());
        IcicleProcessorState icicleProcessorState = new IcicleProcessorState(processingEnv.getTypeUtils(), environmentFactory, logger);
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = env.getElementsAnnotatedWith(annotation);
            icicleProcessorState.process(elements);
        }
        return true;
    }
}