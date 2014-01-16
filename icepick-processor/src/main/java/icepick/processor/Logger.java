package icepick.processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

class Logger {

  private final Messager messager;

  public Logger(Messager messager) {
    this.messager = messager;
  }

  public void logError(String message, Object... args) {
    messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, args));
  }

  public void logError(Element element, String message, Object... args) {
    messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, args), element);
  }

  public void logError(Throwable throwable) {
    messager.printMessage(Diagnostic.Kind.ERROR, throwable.getMessage());
  }
}
