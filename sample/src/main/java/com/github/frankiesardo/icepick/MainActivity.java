package com.github.frankiesardo.icepick;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.frankiesardo.icepick.bundle.Bundles;

public class MainActivity extends Activity {

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
        CustomView customView = (CustomView) findViewById(R.id.custom);
        customView.setBackgroundWithAnotherMethod(Color.RED);
        super.onSaveInstanceState(outState);
        query = "Ice Pick works!";
        Bundles.saveInstanceState(this, outState);
    }
}
