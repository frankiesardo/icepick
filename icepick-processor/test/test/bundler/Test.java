package test.bundler;

import android.os.Bundle;
import icepick.Bundler;
import icepick.State;

public class Test {
    public static class MyClass {

    }
    public static class StringBundler implements Bundler<String> {

        @Override
        public void put(String s, String s2, Bundle bundle) {

        }

        @Override
        public String get(String s, Bundle bundle) {
            return null;
        }
    }
    public static class MyClassBundler implements Bundler<MyClass> {

        @Override
        public void put(String s, MyClass myClass, Bundle bundle) {

        }

        @Override
        public MyClass get(String s, Bundle bundle) {
            return null;
        }
    }
    @State(Test.MyClassBundler.class) MyClass f1;
    @State(Test.StringBundler.class) String f2;
}
