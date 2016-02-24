package test.views;

import icepick.State;
import android.view.View;
import android.content.Context;
import android.os.Parcelable;
import android.os.Bundle;
public class Test<T extends Parcelable> extends View {
    public Test(Context c) {super(c);}
    @State T f1;
    static class Inner extends Test<Bundle> {
        public Inner(Context c) {super(c);}
        @State String f2;
    }
}