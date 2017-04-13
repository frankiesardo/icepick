package test.views;

import android.os.Bundle;
import android.os.Parcelable;
import icepick.Bundler;
import icepick.Injector.Helper;
import icepick.Injector.View;

import java.util.Map;
import java.util.HashMap;

public class Test$Inner$$Icepick<T extends Test.Inner> extends test.views.Test$$Icepick<T> {

    private final static Map<String, Bundler<?>> BUNDLERS = new HashMap<String, Bundler<?>>();
    static {
    }

    private final static Helper H = new Helper("test.views.Test$Inner$$Icepick.", BUNDLERS);

    @Override public Parcelable restore(T target, Parcelable p) {
        Bundle state = (Bundle) p;
        target.f2 = H.getString(state, "f2");
        return super.restore(target, H.getParent(state));
    }

    @Override public Parcelable save(T target, Parcelable p) {
        Bundle state = H.putParent(super.save(target, p));
        H.putString(state, "f2", target.f2);
        return state;
    }
}