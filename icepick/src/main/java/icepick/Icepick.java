package icepick;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import java.util.LinkedHashMap;
import java.util.Map;

public class Icepick {

  public static final String SUFFIX = "$$Icicle";

  private static final Map<Class<?>, StateHelper<?>> CACHED_HELPERS =
      new LinkedHashMap<Class<?>, StateHelper<?>>();

  public static void saveInstanceState(Object target, Bundle state) {
    new ObjectInjector(target, state, CACHED_HELPERS).inject(Action.SAVE);
  }

  public static void restoreInstanceState(Object target, Bundle state) {
    new ObjectInjector(target, state, CACHED_HELPERS).inject(Action.RESTORE);
  }

  public static <T extends View> Parcelable saveInstanceState(T target, Parcelable state) {
    return new ViewInjector(target, state, CACHED_HELPERS).inject(Action.SAVE);
  }

  public static <T extends View> Parcelable restoreInstanceState(T target, Parcelable state) {
    return new ViewInjector(target, state, CACHED_HELPERS).inject(Action.RESTORE);
  }
}
