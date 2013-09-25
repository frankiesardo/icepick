package icepick.bundle;

import android.os.Bundle;

import java.lang.reflect.Method;
import java.util.Map;

class BundleFragmentActivityInjector extends BundleInjector<Bundle> {

    BundleFragmentActivityInjector(Object target, Bundle argument, Map<BundleMethodKey, Method> cachedMethods) {
        super(target, argument, cachedMethods);
    }

    void inject(BundleAction action) {
        Class<?> targetClass = target.getClass();
        try {
            Method inject = getMethodFromHelper(targetClass, action);
            if (inject != null) {
                inject.invoke(null, target, argument);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnableToInjectException(target, e);
        }
    }

    @Override
    protected Class<?> getArgumentClass() {
        return Bundle.class;
    }
}