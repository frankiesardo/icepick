package com.github.frankiesardo.icepick.bundle;

import android.os.Bundle;

public class BundleInjectorTest$$Icicle {

    public static void saveInstanceState(BundleInjectorTest target, Bundle outState) {
        outState.putString("anyKey", target.someField);
    }

    public static void restoreInstanceState(BundleInjectorTest target, Bundle saveInstanceState) {
        target.someField = saveInstanceState.getString("anyKey");
    }
}