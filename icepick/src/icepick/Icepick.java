package icepick;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import java.util.LinkedHashMap;
import java.util.Map;

public class Icepick {

    public static final String SUFFIX = "$$Icepick";
    public static final String ANDROID_PREFIX = "android.";
    public static final String JAVA_PREFIX = "java.";

    private static final String TAG = "Icepick";

    private static boolean debug = false;

    public static void setDebug(boolean debug) {
        Icepick.debug = debug;
    }

    private static final Injector.Object DEFAULT_OBJECT_INJECTOR = new Injector.Object();
    private static final Injector.View DEFAULT_VIEW_INJECTOR = new Injector.View();
    private static final Map<Class<?>, Injector> INJECTORS =
        new LinkedHashMap<Class<?>, Injector>();

    private static Injector getInjector(Class<?> cls)
        throws IllegalAccessException, InstantiationException {
        Injector injector = INJECTORS.get(cls);
        if (injector != null) {
            if (debug) Log.d(TAG, "HIT: Cached in injector map.");
            return injector;
        }
        String clsName = cls.getName();
        if (clsName.startsWith(ANDROID_PREFIX) || clsName.startsWith(JAVA_PREFIX)) {
            if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            return null;
        }
        try {
            Class<?> injectorClass = Class.forName(clsName + SUFFIX);
            injector = (Injector) injectorClass.newInstance();
            if (debug) Log.d(TAG, "HIT: Class loaded injection class.");
        } catch (ClassNotFoundException e) {
            if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            injector = getInjector(cls.getSuperclass());
        }
        INJECTORS.put(cls, injector);
        return injector;
    }

    private static <T extends Injector> T safeGet(Object target, Injector nop) {
        try {
            Class<?> targetClass = target.getClass();
            Injector injector = getInjector(targetClass);
            if (injector == null) {
                injector = nop;
            }
            return (T) injector;
        } catch (Exception e) {
            throw new RuntimeException("Unable to inject state for " + target, e);
        }
    }

    public static <T> void saveInstanceState(T target, Bundle state) {
        Injector.Object<T> injector = safeGet(target, DEFAULT_OBJECT_INJECTOR);
        injector.save(target, state);
    }

    public static <T> void restoreInstanceState(T target, Bundle state) {
        Injector.Object<T> injector = safeGet(target, DEFAULT_OBJECT_INJECTOR);
        injector.restore(target, state);
    }

    public static <T extends View> Parcelable saveInstanceState(T target, Parcelable state) {
        Injector.View<T> injector = safeGet(target, DEFAULT_VIEW_INJECTOR);
        return injector.save(target, state);
    }

    public static <T extends View> Parcelable restoreInstanceState(T target, Parcelable state) {
        Injector.View<T> injector = safeGet(target, DEFAULT_VIEW_INJECTOR);
        return injector.restore(target, state);
    }
}
