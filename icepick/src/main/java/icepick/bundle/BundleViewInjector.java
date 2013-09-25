package icepick.bundle;

import android.os.Parcelable;

import java.lang.reflect.Method;
import java.util.Map;

class BundleViewInjector extends BundleInjector<Parcelable> {

    BundleViewInjector(Object target, Parcelable argument, Map<BundleMethodKey, Method> cachedMethods) {
        super(target, argument, cachedMethods);
    }

    Parcelable inject(BundleAction action) {
        Class<?> targetClass = target.getClass();
        try {
            Method inject = getMethodFromHelper(targetClass, action);
            if (inject != null) {
                return (Parcelable) inject.invoke(null, target, argument);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnableToInjectException(target, e);
        }

        return argument; // return super value
    }

    @Override
    protected Class<?> getArgumentClass() {
        return Parcelable.class;
    }
}
