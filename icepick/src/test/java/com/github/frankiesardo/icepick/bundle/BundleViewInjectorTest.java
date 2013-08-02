package com.github.frankiesardo.icepick.bundle;

import android.os.Parcelable;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;

@PrepareForTest(Parcelable.class)
@RunWith(PowerMockRunner.class)
public class BundleViewInjectorTest {

    Parcelable parcelable = PowerMockito.mock(Parcelable.class);
    BundleViewInjector bundleInjector = new BundleViewInjector(this, parcelable, new HashMap<BundleMethodKey, Method>());

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
}
