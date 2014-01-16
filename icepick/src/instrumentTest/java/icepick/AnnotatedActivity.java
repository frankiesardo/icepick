package icepick;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import java.io.File;
import java.io.Serializable;

class AnnotatedActivity extends Activity {

  @Icicle String aString = "something";
  @Icicle Serializable aSerializable = new File("something");
  @Icicle Parcelable aParcelable = new Bundle();
  @Icicle Object anyObject = new Object();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Icepick.restoreInstanceState(this, savedInstanceState);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }
}
