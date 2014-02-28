package icepick;

enum Action {
  SAVE {
    @Override <T> T invoke(StateHelper<T> helper, Object target, T state) {
      return helper.saveInstanceState(target, state);
    }
  },
  RESTORE {
    @Override <T> T invoke(StateHelper<T> helper, Object target, T state) {
      return helper.restoreInstanceState(target, state);
    }
  };

  abstract <T> T invoke(StateHelper<T> inject, Object target, T state);
}