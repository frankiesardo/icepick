package icepick;

import android.os.Parcelable;
import java.util.Map;

class ViewInjector extends AbsInjector<Parcelable> {

  ViewInjector(Object target, Parcelable argument, Map<Class<?>, StateHelper<?>> cachedHelpers) {
    super(target, argument, cachedHelpers);
  }

  Parcelable inject(Action action) {
    try {
      StateHelper<Parcelable> inject = getHelperForClass(target.getClass());
      if (inject != null) {
        return action.invoke(inject, target, argument);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToInjectException(target, e);
    }

    return argument; // return super value
  }
}
