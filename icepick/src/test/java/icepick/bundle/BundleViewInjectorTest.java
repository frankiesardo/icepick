package icepick.bundle;

import android.os.Parcelable;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

public class BundleViewInjectorTest {

    final Parcelable parcelable = Mockito.mock(Parcelable.class);
    final BundleViewInjector bundleInjector = new BundleViewInjector(new ClassToInject(), parcelable, new HashMap<BundleMethodKey, Method>());

    @Test
    public void shouldRestoreFieldValueWithBundleContent() throws Exception {
        bundleInjector.inject(BundleAction.RESTORE);

        verify(parcelable).describeContents();
    }

    @Test
    public void shouldSaveFieldValueToBundle() throws Exception {
        bundleInjector.inject(BundleAction.SAVE);

        verify(parcelable).describeContents();
    }

    static class ClassToInject {
    }

    static class ClassToInject$$Icicle {

        public static void saveInstanceState(ClassToInject target, Parcelable state) {
            state.describeContents();
        }

        public static void restoreInstanceState(ClassToInject target, Parcelable state) {
            state.describeContents();
        }
    }
}
