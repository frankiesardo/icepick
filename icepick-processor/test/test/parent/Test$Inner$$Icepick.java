package test.parent;

import android.os.Bundle;
import icepick.Bundler;
import icepick.Injector.Helper;
import icepick.Injector.Object;

import java.util.Map;
import java.util.HashMap;

public class Test$Inner$$Icepick<T extends Test.Inner> extends test.parent.Test$$Icepick<T> {

    private final static Map<String, Bundler<?>> BUNDLERS = new HashMap<String, Bundler<?>>();
    static {
    }

    private final static Helper H = new Helper("test.parent.Test$Inner$$Icepick.", BUNDLERS);

    @Override public void restore(T target, Bundle state) {
        if (state == null) return;
        target.f2 = H.getString(state, "f2");
        super.restore(target, state);
    }

    @Override public void save(T target, Bundle state) {
        super.save(target, state);
        H.putString(state, "f2", target.f2);
    }
}
