package test.bundler;
import android.os.Bundle;
import icepick.Bundler;
import icepick.Injector.Helper;
import icepick.Injector.Object;

import java.util.Map;
import java.util.HashMap;

public class Test$$Icepick<T extends Test> extends Object<T> {

    private final static Map<String, Bundler<?>> BUNDLERS = new HashMap<String, Bundler<?>>();
    static {
        BUNDLERS.put("f1", new test.bundler.Test.MyClassBundler());
        BUNDLERS.put("f2", new test.bundler.Test.StringBundler());
    }

    private final static Helper H = new Helper("test.bundler.Test$$Icepick.", BUNDLERS);

    @Override public void restore(T target, Bundle state) {
        if (state == null) return;
        target.f1 = H.getWithBundler(state, "f1");
        target.f2 = H.getWithBundler(state, "f2");
        super.restore(target, state);
    }

    @Override public void save(T target, Bundle state) {
        super.save(target, state);
        H.putWithBundler(state, "f1", target.f1);
        H.putWithBundler(state, "f2", target.f2);
    }
}