package icepick;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class Icepick {

  public static final String SUFFIX = "$$Icicle";

  private static final Map<MethodKey, Method> CACHED_METHODS =
      new LinkedHashMap<MethodKey, Method>();

  @SuppressWarnings("unused")
  public static void saveInstanceState(Object target, Bundle outState) {
    new GenericInjector(target, outState, CACHED_METHODS).inject(Action.SAVE);
  }

  @SuppressWarnings("unused")
  public static void restoreInstanceState(Object target, Bundle outState) {
    new GenericInjector(target, outState, CACHED_METHODS).inject(Action.RESTORE);
  }

  @SuppressWarnings("unused")
  public static <T extends View> Parcelable saveInstanceState(T target, Parcelable state) {
    return new ViewInjector(target, state, CACHED_METHODS).inject(Action.SAVE);
  }

  @SuppressWarnings("unused")
  public static <T extends View> Parcelable restoreInstanceState(T target, Parcelable state) {
    return new ViewInjector(target, state, CACHED_METHODS).inject(Action.RESTORE);
  }

  @SuppressWarnings("unused")
  public static <T> Parcelable wrap(T instance) {
    try {
      String json = GsonParcer.encode(instance);
      return new Wrapper(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unused")
  public static <T> T unwrap(Parcelable parcelable) {
    Wrapper wrapper = (Wrapper) parcelable;
    try {
      return GsonParcer.decode(wrapper.json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
