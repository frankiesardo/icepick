package com.github.frankiesardo.icepick.bundle;

import android.os.Bundle;

public class BundleFragmentActivityInjectorTest$$Icicle {

    public static void saveInstanceState(BundleFragmentActivityInjectorTest target, Bundle outState) {
        outState.putString("anyKey", target.someField);
    }

    public static void restoreInstanceState(BundleFragmentActivityInjectorTest target, Bundle saveInstanceState) {
        target.someField = saveInstanceState.getString("anyKey");
    }
}