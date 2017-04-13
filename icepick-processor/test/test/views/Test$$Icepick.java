package test.views;
import android.os.Bundle;
import android.os.Parcelable;
import icepick.Bundler;
import icepick.Injector.Helper;
import icepick.Injector.View;

import java.util.Map;
import java.util.HashMap;

public class Test$$Icepick<T extends Test> extends View<T> {

    private final static Map<String, Bundler<?>> BUNDLERS = new HashMap<String, Bundler<?>>();
    static {
    }

    private final static Helper H = new Helper("test.views.Test$$Icepick.", BUNDLERS);

    @Override public Parcelable restore(T target, Parcelable p) {
        Bundle state = (Bundle) p;
        target.f1 = H.getParcelable(state, "f1");
        return super.restore(target, H.getParent(state));
    }

    @Override public Parcelable save(T target, Parcelable p) {
        Bundle state = H.putParent(super.save(target, p));
        H.putParcelable(state, "f1", target.f1);
        return state;
    }
}