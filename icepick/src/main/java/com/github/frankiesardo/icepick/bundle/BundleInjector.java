package com.github.frankiesardo.icepick.bundle;

import android.os.Bundle;

import com.github.frankiesardo.icepick.annotation.IcicleProcessor;

import java.lang.reflect.Method;
import java.util.Map;

class BundleInjector {

    static final Method NO_OP = null;

    private final Object target;
    private final Bundle bundle;
    private final Map<BundleMethodKey, Method> cachedMethods;

    public BundleInjector(Object target, Bundle bundle, Map<BundleMethodKey, Method> cachedMethods) {
        this.target = target;
        this.bundle = bundle;
        this.cachedMethods = cachedMethods;
    }

    public void inject(BundleAction action) {
        Class<?> targetClass = target.getClass();
        try {
            Method inject = getMethodFromHelper(targetClass, action);
            if (inject != null) {
                inject.invoke(null, target, bundle);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnableToInjectException("Unable to manipulate bundle for " + target, e);
        }
    }

    private Method getMethodFromHelper(Class<?> cls, BundleAction bundleAction) throws NoSuchMethodException {
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
            method = helper.getMethod(bundleAction.name, cls, Bundle.class);
        } catch (ClassNotFoundException e) {
            method = getMethodFromHelper(cls.getSuperclass(), bundleAction);
        }
        cachedMethods.put(bundleMethodKey, method);
        return method;
    }

    public static class UnableToInjectException extends RuntimeException {
        UnableToInjectException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
