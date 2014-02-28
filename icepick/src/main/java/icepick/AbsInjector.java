package icepick;

import java.util.Map;

abstract class AbsInjector<T> {

  protected final Object target;
  protected final T argument;
  protected final Map<Class<?>, StateHelper<?>> cachedHelpers;

  protected AbsInjector(Object target, T argument, Map<Class<?>, StateHelper<?>> cachedHelpers) {
    this.target = target;
    this.argument = argument;
    this.cachedHelpers = cachedHelpers;
  }

  @SuppressWarnings("unchecked")
  protected StateHelper<T> getHelperForClass(Class<?> cls) throws NoSuchMethodException {
    StateHelper<T> stateHelper = (StateHelper<T>) cachedHelpers.get(cls);
    if (stateHelper != null) {
      return stateHelper;
    }

    String clsName = cls.getName();
    if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
      return (StateHelper<T>) StateHelper.NO_OP;
    }

    try {
      stateHelper = (StateHelper<T>) Class.forName(clsName + Icepick.SUFFIX).newInstance();
    } catch (ClassNotFoundException e) {
      stateHelper = getHelperForClass(cls.getSuperclass());
    } catch (InstantiationException e) {
      return (StateHelper<T>) StateHelper.NO_OP;
    } catch (IllegalAccessException e) {
      return (StateHelper<T>) StateHelper.NO_OP;
    }

    cachedHelpers.put(cls, stateHelper);
    return stateHelper;
  }

  public static class UnableToInjectException extends RuntimeException {
    UnableToInjectException(Object target, Throwable cause) {
      super("Unable to inject class " + target, cause);
    }
  }
}
