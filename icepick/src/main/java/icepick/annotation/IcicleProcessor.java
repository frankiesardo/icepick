package icepick.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;

@SupportedAnnotationTypes("com.github.frankiesardo.icepick.icepick.bundle.icepick.annotation.Icicle")
public class IcicleProcessor extends AbstractProcessor {

    public static final String SUFFIX = "$$Icicle";

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment env) {
        Types typeUtils = processingEnv.getTypeUtils();
        Elements elementUtils = processingEnv.getElementUtils();
        IcicleFactory factory = new IcicleFactory(typeUtils, elementUtils, processingEnv.getFiler(), SUFFIX);
        IcicleLogger icicleLogger = new IcicleLogger(processingEnv.getMessager());
        IcicleProcessorState icicleProcessorState = new IcicleProcessorState(typeUtils, factory, icicleLogger);
        icicleProcessorState.process(env.getElementsAnnotatedWith(Icicle.class));
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}