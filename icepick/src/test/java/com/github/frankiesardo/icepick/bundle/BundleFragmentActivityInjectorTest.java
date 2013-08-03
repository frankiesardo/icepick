package com.github.frankiesardo.icepick.bundle;

import android.os.Bundle;

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
public class BundleFragmentActivityInjectorTest {

    String someField;

    Bundle bundle = PowerMockito.mock(Bundle.class);
    BundleFragmentActivityInjector bundleFragmentActivityInjector = new BundleFragmentActivityInjector(this, bundle, new HashMap<BundleMethodKey, Method>());

    @Test
    public void shouldRestoreFieldValueWithBundleContent() throws Exception {
        String value = "foo";
        when(bundle.getString(anyString())).thenReturn(value);
        bundleFragmentActivityInjector.inject(BundleAction.RESTORE);
        assertEquals(value, someField);
    }

    @Test
    public void shouldSaveFieldValueToBundle() throws Exception {
        String value = "foo";
        someField = value;
        bundleFragmentActivityInjector.inject(BundleAction.SAVE);
        verify(bundle).putString(anyString(), eq(value));
    }
}
