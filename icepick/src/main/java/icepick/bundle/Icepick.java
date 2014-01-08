package icepick.bundle;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class Icepick {

    public static final String SUFFIX = "$$Icicle";

    private static final Map<MethodKey, Method> CACHED_METHODS = new LinkedHashMap<MethodKey, Method>();

    public static <T extends Activity> void saveInstanceState(T target, Bundle outState) {
        save(target, outState);
    }

    public static <T extends Fragment> void saveInstanceState(T target, Bundle outState) {
        save(target, outState);
    }

    public static <T extends android.support.v4.app.Fragment> void saveInstanceState(T target, Bundle outState) {
        save(target, outState);
    }

    public static <T extends View> Parcelable saveInstanceState(T target, Parcelable state) {
        return new ViewInjector(target, state, CACHED_METHODS).inject(Action.SAVE);
    }

    private static void save(Object target, Bundle outState) {
        new FragmentActivityInjector(target, outState, CACHED_METHODS).inject(Action.SAVE);
    }

    public static <T extends Activity> void restoreInstanceState(T target, Bundle savedInstanceState) {
        restore(target, savedInstanceState);
    }

    public static <T extends Fragment> void restoreInstanceState(T target, Bundle savedInstanceState) {
        restore(target, savedInstanceState);
    }

    public static <T extends android.support.v4.app.Fragment> void restoreInstanceState(T target, Bundle savedInstanceState) {
        restore(target, savedInstanceState);
    }

    private static void restore(Object target, Bundle savedInstanceState) {
        new FragmentActivityInjector(target, savedInstanceState, CACHED_METHODS).inject(Action.RESTORE);
    }

    public static <T extends View> Parcelable restoreInstanceState(T target, Parcelable state) {
        return new ViewInjector(target, state, CACHED_METHODS).inject(Action.RESTORE);
    }
}
