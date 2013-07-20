package com.github.frankiesardo.icepick;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.frankiesardo.icepick.annotation.Icicle;
import com.github.frankiesardo.icepick.bundle.Bundles;

public class MainActivity extends Activity {

    @Icicle
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundles.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.something);
        textView.setText(query == null ? " Turn your device" : query);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        query = "Ice Pick works!";
        Bundles.saveInstanceState(this, outState);
    }
}
