package icepick;

import android.os.Bundle;
import android.test.AndroidTestCase;

import static icepick.Icepick.restoreInstanceState;
import static icepick.Icepick.saveInstanceState;

public class IcepickTest extends AndroidTestCase {

  public void testSaveAndRestoreInstanceState() throws Exception {
    ClassToInject classToInject = new ClassToInject();
    Bundle state = new Bundle();

    classToInject.string = "string";
    saveInstanceState(classToInject, state);

    classToInject.string = null;
    restoreInstanceState(classToInject, state);

    assertEquals(classToInject.string, "string");
  }

  static class ClassToInject {
    String string;
  }

  @SuppressWarnings("unused")
  static class ClassToInject$$Icicle {

    public static void saveInstanceState(ClassToInject target, Bundle outState) {
      outState.putString("KEY", target.string);
    }

    public static void restoreInstanceState(ClassToInject target, Bundle saveInstanceState) {
      target.string = saveInstanceState.getString("KEY");
    }
  }
}
