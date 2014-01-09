package icepick;

import android.os.Bundle;

import java.lang.reflect.Method;
import java.util.Map;

class FragmentActivityInjector extends Injector<Bundle> {

    FragmentActivityInjector(Object target, Bundle argument, Map<MethodKey, Method> cachedMethods) {
        super(target, argument, cachedMethods);
    }

    void inject(Action action) {
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