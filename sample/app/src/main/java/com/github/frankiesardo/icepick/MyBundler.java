package com.github.frankiesardo.icepick;

import android.os.Bundle;

import icepick.Bundler;

class MyBundler implements Bundler<String> {
    @Override
    public void put(String key, String value, Bundle bundle) {
        bundle.putString(key, value + "*");
    }

    @Override
    public String get(String key, Bundle bundle) {
        return bundle.getString(key);
    }

}
