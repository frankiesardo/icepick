package icepick.processor;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@SupportedAnnotationTypes("icepick.Icicle")
public class IcicleProcessor extends AbstractProcessor {

  private Logger logger;
  private Elements elementUtils;
  private Types typeUtils;
  private Filer filer;

  @Override public synchronized void init(ProcessingEnvironment environment) {
    super.init(environment);
    logger = new Logger(processingEnv.getMessager());
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();
    filer = processingEnv.getFiler();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
    for (TypeElement annotation : annotations) {
      write(classesWithFieldsAnnotatedWith(environment.getElementsAnnotatedWith(annotation)));
    }
    return true;
  }

  private void write(Map<EnclosingClass, Collection<AnnotatedField>> fieldsByEnclosingClass) {
    ClassWriter classWriter = new ClassWriter(elementUtils, typeUtils, filer, "$$Icicle");
    if (false) throw new RuntimeException(dump(fieldsByEnclosingClass));
    for (EnclosingClass enclosingClass : fieldsByEnclosingClass.keySet()) {
      try {
        classWriter.writeClass(enclosingClass).withFields(fieldsByEnclosingClass.get(enclosingClass));
      } catch (IOException e) {
        //logger.logError("Impossible to generate class %. Reason: %" + enclosingClass.getTargetClass(), e);
        throw new RuntimeException(e);
      }
    }
  }

  private String dump(Map<EnclosingClass, Collection<AnnotatedField>> fieldsByEnclosingClass) {
    StringBuilder builder = new StringBuilder("\n");
    for (EnclosingClass enclosingClass : fieldsByEnclosingClass.keySet()) {
      builder.append("> Class: " + enclosingClass.getClassName() + "\n");
      for (AnnotatedField field : fieldsByEnclosingClass.get(enclosingClass)) {
        builder.append("    > Field: " + field.getName() + "\n");
      }
    }
    return builder.toString();
  }

  private Map<EnclosingClass, Collection<AnnotatedField>> classesWithFieldsAnnotatedWith(
      Set<? extends Element> annotatedElements) {
    return new AnnotationsConverter(logger, elementUtils, typeUtils).convert(annotatedElements);
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.RELEASE_6;
  }
}