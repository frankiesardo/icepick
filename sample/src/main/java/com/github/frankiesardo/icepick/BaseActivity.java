package com.github.frankiesardo.icepick;

import android.app.Activity;
import android.os.Bundle;

import icepick.annotation.Icicle;
import icepick.bundle.Bundles;

public class BaseActivity extends Activity {

    @Icicle
    String baseMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundles.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundles.saveInstanceState(this, outState);
    }
}
