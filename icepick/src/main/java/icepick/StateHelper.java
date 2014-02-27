package icepick;

public interface StateHelper<T> {

  T saveInstanceState(Object target, T state);

  T restoreInstanceState(Object target, T state);
}
