package com.github.frankiesardo.icepick.bundle;

import android.os.Parcelable;

import com.github.frankiesardo.icepick.annotation.IcicleProcessor;

import java.lang.reflect.Method;
import java.util.Map;

class BundleViewInjector {

    static final Method NO_OP = null;

    private final Object target;
    private final Parcelable parcelable;
    private final Map<BundleMethodKey, Method> cachedMethods;

    public BundleViewInjector(Object target, Parcelable parcelable, Map<BundleMethodKey, Method> cachedMethods) {
        this.target = target;
        this.parcelable = parcelable;
        this.cachedMethods = cachedMethods;
    }

    public Parcelable inject(BundleAction action) {
        Class<?> targetClass = target.getClass();
        try {
            Method inject = getMethodFromHelper(targetClass, action);
            if (inject != null) {
                return (Parcelable) inject.invoke(null, target, parcelable);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnableToInjectException("Unable to manipulate parcelable for " + target, e);
        }

        return parcelable; // return super value
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
            method = helper.getMethod(bundleAction.name, cls, Parcelable.class);
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

        UnableToInjectException(String message) {
            super(message);
        }
    }
}
