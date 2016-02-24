package test.generics;

import icepick.State;

import java.util.ArrayList;

import android.util.SparseArray;
import android.os.Parcelable;

public class Test3<T extends Parcelable> {
    @State ArrayList<T> f1;
    @State SparseArray<T> f2;
}