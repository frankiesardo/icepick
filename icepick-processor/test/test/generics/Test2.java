package test.generics;


import icepick.State;
import android.os.Parcelable;

public class Test2<T extends Parcelable> {
    @State
    T f1;
}