package test.generics;

import icepick.State;
import java.util.ArrayList;
import android.os.Bundle;

public class Test4 {
    static class AL<T> extends ArrayList<T> {}
    @State AL<Integer> f1;
    @State AL<String> f2;
    @State AL<CharSequence> f3;
    @State Bundle[] f4;
    @State StringBuffer[] f5;
}