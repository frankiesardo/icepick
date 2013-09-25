package icepick.bundle;

import java.lang.reflect.Method;
import java.util.Map;

import icepick.annotation.IcicleProcessor;

abstract class BundleInjector<T> {

    static final Method NO_OP = null;

    protected final Object target;
    protected final T argument;
    protected final Map<BundleMethodKey, Method> cachedMethods;

    protected BundleInjector(Object target, T argument, Map<BundleMethodKey, Method> cachedMethods) {
        this.target = target;
        this.argument = argument;
        this.cachedMethods = cachedMethods;
    }

    protected Method getMethodFromHelper(Class<?> cls, BundleAction bundleAction) throws NoSuchMethodException {
        BundleMethodKey bundleMethodKey = new BundleMethodKey(cls, bundleAction);
        Method method = cachedMethods.get(bundleMethodKey);
        if (method != null) {
            return method;
        }

        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            return NO_OP;
        }

        try {
            Class<?> helper = Class.forName(clsName + IcicleProcessor.SUFFIX);
            method = helper.getMethod(bundleAction.name, cls, getArgumentClass());
        } catch (ClassNotFoundException e) {
            method = getMethodFromHelper(cls.getSuperclass(), bundleAction);
        }
        cachedMethods.put(bundleMethodKey, method);
        return method;
    }

    protected abstract Class<?> getArgumentClass();

    public static class UnableToInjectException extends RuntimeException {
        UnableToInjectException(Object target, Throwable cause) {
            super("Unable to manipulate icepick.bundle for " + target, cause);
        }
    }
}
