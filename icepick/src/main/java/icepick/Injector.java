package icepick;

import java.lang.reflect.Method;
import java.util.Map;

abstract class Injector<T> {

  static final Method NO_OP = null;
  protected final Object target;
  protected final T argument;
  protected final Map<MethodKey, Method> cachedMethods;

  protected Injector(Object target, T argument, Map<MethodKey, Method> cachedMethods) {
    this.target = target;
    this.argument = argument;
    this.cachedMethods = cachedMethods;
  }

  protected Method getMethodFromHelper(Class<?> cls, Action action) throws NoSuchMethodException {
    MethodKey methodKey = new MethodKey(cls, action);
    Method method = cachedMethods.get(methodKey);
    if (method != null) {
      return method;
    }

    String clsName = cls.getName();
    if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
      return NO_OP;
    }

    try {
      Class<?> helper = Class.forName(clsName + Icepick.SUFFIX);
      method = helper.getMethod(action.name, cls, getArgumentClass());
    } catch (ClassNotFoundException e) {
      method = getMethodFromHelper(cls.getSuperclass(), action);
    }
    cachedMethods.put(methodKey, method);
    return method;
  }

  protected abstract Class<?> getArgumentClass();

  public static class UnableToInjectException extends RuntimeException {
    UnableToInjectException(Object target, Throwable cause) {
      super("Unable to inject class " + target, cause);
    }
  }
}
