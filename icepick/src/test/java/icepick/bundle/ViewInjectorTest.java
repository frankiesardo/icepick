package icepick.bundle;

import android.os.Parcelable;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

public class ViewInjectorTest {

    final Parcelable parcelable = Mockito.mock(Parcelable.class);
    final ViewInjector viewInjector = new ViewInjector(new ClassToInject(), parcelable, new HashMap<MethodKey, Method>());

    @Test
    public void shouldRestoreFieldValueWithBundleContent() throws Exception {
        viewInjector.inject(Action.RESTORE);

        verify(parcelable).describeContents();
    }

    @Test
    public void shouldSaveFieldValueToBundle() throws Exception {
        viewInjector.inject(Action.SAVE);

        verify(parcelable).describeContents();
    }

    static class ClassToInject {
    }

    static class ClassToInject$$Icepick {

        public static void saveInstanceState(ClassToInject target, Parcelable state) {
            state.describeContents();
        }

        public static void restoreInstanceState(ClassToInject target, Parcelable state) {
            state.describeContents();
        }
    }
}
