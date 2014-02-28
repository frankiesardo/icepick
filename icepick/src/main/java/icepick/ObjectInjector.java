package icepick;

import android.os.Bundle;
import java.util.Map;

class ObjectInjector extends AbsInjector<Bundle> {

  ObjectInjector(Object target, Bundle argument, Map<Class<?>, StateHelper<?>> cachedHelpers) {
    super(target, argument, cachedHelpers);
  }

  void inject(Action action) {
    try {
      StateHelper<Bundle> helper = getHelperForClass(target.getClass());
      if (helper != null) {
        action.invoke(helper, target, argument);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToInjectException(target, e);
    }
  }
}