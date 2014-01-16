package icepick;

import android.os.Bundle;
import android.test.AndroidTestCase;

public class AcceptanceTest extends AndroidTestCase {

  public void testAcceptance() throws Exception {
    AnnotatedActivity activity = new AnnotatedActivity();
    activity.onCreate(null);
    Bundle outState = new Bundle();
    activity.onSaveInstanceState(outState);
    activity.onCreate(outState);
  }
}
