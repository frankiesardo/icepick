package icepick;

public interface StateHelper<T> {

  T saveInstanceState(Object target, T state);

  T restoreInstanceState(Object target, T state);

  public static StateHelper<?> NO_OP = new StateHelper<Object>() {
    @Override public Object saveInstanceState(Object target, Object state) {
      return state;
    }

    @Override public Object restoreInstanceState(Object target, Object state) {
      return state;
    }
  };
}
