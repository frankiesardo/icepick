package test.generics;
import android.os.Bundle;
import icepick.Bundler;
import icepick.Injector.Helper;
import icepick.Injector.Object;

import java.util.Map;
import java.util.HashMap;

public class Test3$$Icepick<T extends Test3> extends Object<T> {

    private final static Map<String, Bundler<?>> BUNDLERS = new HashMap<String, Bundler<?>>();
    static {
    }

    private final static Helper H = new Helper("test.generics.Test3$$Icepick.", BUNDLERS);

    @Override public void restore(T target, Bundle state) {
        if (state == null) return;
        target.f1 = H.getParcelableArrayList(state, "f1");
        target.f2 = H.getSparseParcelableArray(state, "f2");
        super.restore(target, state);
    }

    @Override public void save(T target, Bundle state) {
        super.save(target, state);
        H.putParcelableArrayList(state, "f1", target.f1);
        H.putSparseParcelableArray(state, "f2", target.f2);
    }
}