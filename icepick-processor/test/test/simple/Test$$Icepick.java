package test.simple;

import android.os.Bundle;
import icepick.Bundler;
import icepick.Injector.Helper;
import icepick.Injector.Object;

import java.util.Map;
import java.util.HashMap;


public class Test$$Icepick<T extends Test> extends Object<T> {
    private final static Map<String, Bundler<?>> BUNDLERS = new HashMap<String, Bundler<?>>();
    static {
    }

    private final static Helper H = new Helper("test.simple.Test$$Icepick.", BUNDLERS);

    @Override public void restore(T target, Bundle state) {
        if (state == null) return;
        target.f1 = H.getInt(state, "f1");
        target.f2 = H.getBoolean(state, "f2");
        target.f3 = H.getCharArray(state, "f3");
        super.restore(target, state);
    }

    @Override public void save(T target, Bundle state) {
        super.save(target, state);
        H.putInt(state, "f1", target.f1);
        H.putBoolean(state, "f2", target.f2);
        H.putCharArray(state, "f3", target.f3);
    }
}