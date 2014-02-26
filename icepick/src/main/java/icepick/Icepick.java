package icepick;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class Icepick {

  public static final String SUFFIX = "$$Icicle";

  private static final Map<MethodKey, Method> CACHED_METHODS =
      new LinkedHashMap<MethodKey, Method>();

  public static void saveInstanceState(Object target, Bundle outState) {
    new GenericInjector(target, outState, CACHED_METHODS).inject(Action.SAVE);
  }

  public static void restoreInstanceState(Object target, Bundle outState) {
    new GenericInjector(target, outState, CACHED_METHODS).inject(Action.RESTORE);
  }

  public static <T extends View> Parcelable saveInstanceState(T target, Parcelable state) {
    return new ViewInjector(target, state, CACHED_METHODS).inject(Action.SAVE);
  }

  public static <T extends View> Parcelable restoreInstanceState(T target, Parcelable state) {
    return new ViewInjector(target, state, CACHED_METHODS).inject(Action.RESTORE);
  }
}
