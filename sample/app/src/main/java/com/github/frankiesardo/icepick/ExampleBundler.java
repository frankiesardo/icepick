package com.github.frankiesardo.icepick;

import android.os.Bundle;

import org.parceler.Parcels;

import icepick.Bundler;

public class ExampleBundler implements Bundler<Object> {
    @Override
    public void put(String s, Object example, Bundle bundle) {
        bundle.putParcelable(s, Parcels.wrap(example));
    }

    @Override
    public Object get(String s, Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(s));
    }
}
