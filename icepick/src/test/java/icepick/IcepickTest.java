package icepick;

import android.os.Bundle;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(Bundle.class) @RunWith(PowerMockRunner.class)
public class IcepickTest {

  static final String KEY = "key";
  static final String VALUE = "value";
  static final String ANOTHER_VALUE = "anotherValue";

  final Bundle state = PowerMockito.mock(Bundle.class);
  final ClassToInject classToInject = new ClassToInject();

  @Test public void saveState() throws Exception {
    Icepick.saveInstanceState(classToInject, state);

    verify(state).putString(KEY, VALUE);
  }

  @Test public void restoreState() throws Exception {
    when(state.getString(KEY)).thenReturn(ANOTHER_VALUE);

    Icepick.restoreInstanceState(classToInject, state);

    Assert.assertEquals(ANOTHER_VALUE, classToInject.string);
  }

  static class ClassToInject {
    String string = VALUE;
  }

  @SuppressWarnings("unused")
  static class ClassToInject$$Icicle {

    public static void saveInstanceState(ClassToInject target, Bundle outState) {
      outState.putString(KEY, target.string);
    }

    public static void restoreInstanceState(ClassToInject target, Bundle saveInstanceState) {
      target.string = saveInstanceState.getString(KEY);
    }
  }
}
