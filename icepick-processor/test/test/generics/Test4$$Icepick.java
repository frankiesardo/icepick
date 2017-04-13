package test.generics;
import android.os.Bundle;
import icepick.Bundler;
import icepick.Injector.Helper;
import icepick.Injector.Object;

import java.util.Map;
import java.util.HashMap;

public class Test4$$Icepick<T extends Test4> extends Object<T> {

    private final static Map<String, Bundler<?>> BUNDLERS = new HashMap<String, Bundler<?>>();
    static {
    }

    private final static Helper H = new Helper("test.generics.Test4$$Icepick.", BUNDLERS);

    @Override public void restore(T target, Bundle state) {
        if (state == null) return;
        target.f1 = H.getSerializable(state, "f1");
        target.f2 = H.getSerializable(state, "f2");
        target.f3 = H.getSerializable(state, "f3");
        target.f4 = H.getSerializable(state, "f4");
        target.f5 = H.getSerializable(state, "f5");
        super.restore(target, state);
    }

    @Override public void save(T target, Bundle state) {
        super.save(target, state);
        H.putSerializable(state, "f1", target.f1);
        H.putSerializable(state, "f2", target.f2);
        H.putSerializable(state, "f3", target.f3);
        H.putSerializable(state, "f4", target.f4);
        H.putSerializable(state, "f5", target.f5);
    }
}