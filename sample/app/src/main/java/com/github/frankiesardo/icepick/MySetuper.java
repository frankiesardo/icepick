package com.github.frankiesardo.icepick;

import icepick.Setuper;

class MySetuper implements Setuper<MainActivity, String> {
    @Override
    public void setup(MainActivity target, String value) {
        if (value != null) {
            target.message = value;
        }
    }
}
