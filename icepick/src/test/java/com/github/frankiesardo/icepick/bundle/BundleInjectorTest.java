package com.github.frankiesardo.icepick.bundle;

import android.os.Bundle;

import com.github.frankiesardo.icepick.annotation.Icicle;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(Bundle.class)
@RunWith(PowerMockRunner.class)
public class BundleInjectorTest {

    @Icicle
    String someField;

    Bundle bundle = PowerMockito.mock(Bundle.class);
    BundleInjector bundleInjector = new BundleInjector(this, bundle, new HashMap<BundleMethodKey, Method>());

    @Test
    public void shouldRestoreFieldValueWithBundleContent() throws Exception {
        String value = "foo";
        when(bundle.getString(anyString())).thenReturn(value);
        bundleInjector.inject(BundleAction.RESTORE);
        assertEquals(someField, value);
    }

    @Test
    public void shouldSaveFieldValueToBundle() throws Exception {
        String value = "foo";
        someField = value;
        bundleInjector.inject(BundleAction.SAVE);
        verify(bundle).putString(anyString(), eq(value));
    }
}
