package com.sample.icepick.lib;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import icepick.Icepick;
import icepick.State;
import android.util.Log;

public class BaseActivity extends Activity {
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
