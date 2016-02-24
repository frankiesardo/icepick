package test.parent;

import icepick.State;
import android.os.Parcelable;
import android.os.Bundle;
public class Test<T extends Parcelable> {
    @State T f1;
    static class Inner extends Test<Bundle> {
        @State String f2;
    }
}
