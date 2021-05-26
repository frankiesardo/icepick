package com.sample.icepick.lib;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import icepick.Icepick;
import icepick.State;

public class BaseActivity extends AppCompatActivity {
  @State protected String baseMessage;

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
