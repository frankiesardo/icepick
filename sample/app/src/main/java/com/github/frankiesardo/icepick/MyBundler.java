package com.github.frankiesardo.icepick;

import android.os.Bundle;

import icepick.Bundler;

public class MyBundler implements Bundler<String> {
    @Override
    public void put(String key, String value, Bundle bundle) {
        bundle.putString(key, value + "*");
    }

    @Override
    public <V extends String> V get(String key, Bundle bundle) {
        return (V) bundle.getString(key);
    }
}
