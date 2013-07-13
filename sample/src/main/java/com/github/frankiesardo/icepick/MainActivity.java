package com.github.frankiesardo.icepick;

import com.github.frankiesardo.icepick.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.frankiesardo.icepick.annotation.Icicle;
import com.github.frankiesardo.icepick.bundle.Bundles;

public class MainActivity extends Activity {

    @Icicle
    String query;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundles.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.something);
        textView.setText(query == null ? " nothing" : query);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        query = "Foo";
        Bundles.saveInstanceState(this, outState);
    }
}
