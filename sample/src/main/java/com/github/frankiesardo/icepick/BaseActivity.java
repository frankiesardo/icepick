package com.github.frankiesardo.icepick;

import android.app.Activity;
import android.os.Bundle;

import icepick.Icicle;
import icepick.Icepick;

public class BaseActivity extends Activity {

    @Icicle
    String baseMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
