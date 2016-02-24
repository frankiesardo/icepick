package icepick.processor;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import com.google.auto.service.AutoService;
import icepick.State;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@AutoService(Processor.class)
public class IcepickProcessor extends AbstractProcessor {

    private static IFn PROCESS;

    private static void loadClojureFn() {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(IcepickProcessor.class.getClassLoader());
        try {
            Object ns = Clojure.read("icepick.processor");
            IFn require = Clojure.var("clojure.core", "require");
            require.invoke(ns);
            PROCESS = Clojure.var("icepick.processor", "process");
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    static {
        loadClojureFn();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        PROCESS.invoke(processingEnv, annotations, environment);
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(Collections.singletonList(State.class.getName()));
    }
}
