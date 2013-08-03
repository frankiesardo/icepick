package com.github.frankiesardo.icepick;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.frankiesardo.icepick.annotation.Icicle;
import com.github.frankiesardo.icepick.bundle.Bundles;

public class MainActivity extends Activity {

    @Icicle
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundles.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.message)).setText(message == null ? " Turn your device" : message);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ((CustomView) findViewById(R.id.custom_view)).setBackgroundWithAnotherMethod(Color.RED);
        super.onSaveInstanceState(outState);

        message = "Ice Pick works!";
        Bundles.saveInstanceState(this, outState);
    }
}
