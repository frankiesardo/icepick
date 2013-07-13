package com.github.frankiesardo.icepick.bundle;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class Bundles {

    private static final Map<BundleMethodKey, Method> CACHED_METHODS = new LinkedHashMap<BundleMethodKey, Method>();

    public static <T extends Activity> void saveInstanceState(T target, Bundle outState) {
        save(target, outState);
    }

    public static <T extends Fragment> void saveInstanceState(T target, Bundle outState) {
        save(target, outState);
    }

    //Gradle is giving problems atm
//    public static <T extends android.support.v4.app.Fragment> void saveInstanceState(T target, Bundle outState) {
//        new BundleInjector(target, outState, CACHED_METHODS).inject(BundleAction.SAVE);
//	}

    private static void save(Object target, Bundle outState) {
        new BundleInjector(target, outState, CACHED_METHODS).inject(BundleAction.SAVE);
    }

    public static <T extends Activity> void restoreInstanceState(T target, Bundle savedInstanceState) {
        restore(target, savedInstanceState);
    }

    public static <T extends Fragment> void restoreInstanceState(T target, Bundle savedInstanceState) {
        restore(target, savedInstanceState);
    }
    //Gradle is giving problems atm
//    public static <T extends android.support.v4.app.Fragment> void restoreInstanceState(T target, Bundle savedInstanceState) {
//        new BundleInjector(target, savedInstanceState, CACHED_METHODS).inject(BundleAction.RESTORE);
//   }

    private static void restore(Object target, Bundle savedInstanceState) {
        new BundleInjector(target, savedInstanceState, CACHED_METHODS).inject(BundleAction.RESTORE);
    }

}
