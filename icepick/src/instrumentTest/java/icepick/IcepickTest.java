package icepick;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.test.AndroidTestCase;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static icepick.Icepick.restoreInstanceState;
import static icepick.Icepick.saveInstanceState;
import static icepick.Icepick.unwrap;
import static icepick.Icepick.wrap;

public class IcepickTest extends AndroidTestCase {

  public void testWrapAndUnwrap() throws Exception {
    String string = "string";
    int primitive = Integer.MAX_VALUE;
    Object object = Action.SAVE;
    Serializable serializable = new File("some/path");
    Parcelable parcelable = Uri.parse("http://google.com");
    List<String> arrayList = Arrays.asList("one", "two", "three");

    assertIdempotent(string);
    assertIdempotent(primitive);
    assertIdempotent(object);
    assertIdempotent(serializable);
    assertIdempotent(parcelable);
    assertIdempotent(arrayList);
  }

  public void testThrowsExceptionForNonWrappableInstances() throws Exception {
    try {
      assertIdempotent(new Bundle());
      fail();
    } catch(Icepick.UnableToSerializeException e) {}
  }

  public void testNullSafe() throws Exception {
    assertIdempotent(null);
  }

  private void assertIdempotent(Object o) {
    assertEquals(o, unwrap(wrap(o)));
  }

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
