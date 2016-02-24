package icepick;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Injector {

    public static class Helper {

        private final String baseKey;
        private final Map<String, Bundler<?>> bundlers;

        public Helper(String baseKey, Map<String, Bundler<?>> bundlers) {
            this.baseKey = baseKey;
            this.bundlers = bundlers;
        }

        public <T> T getWithBundler(Bundle state, String key) {
            Bundler<T> b = (Bundler<T>) bundlers.get(key);
            return b.get(key + baseKey, state);
        }

        public <T> void putWithBundler(Bundle state, String key, T value) {
            Bundler<T> b = (Bundler<T>) bundlers.get(key);
            b.put(key + baseKey, value, state);
        }

        public boolean getBoolean(Bundle state, String key) {
            return state.getBoolean(key + baseKey);
        }

        public void putBoolean(Bundle state, String key, boolean x) {
            state.putBoolean(key + baseKey, x);
        }

        public Boolean getBoxedBoolean(Bundle state, String key) {
            if (state.containsKey(key + baseKey)) {
                return state.getBoolean(key + baseKey);
            }
            return null;
        }

        public void putBoxedBoolean(Bundle state, String key, Boolean x) {
            if (x != null) {
                state.putBoolean(key + baseKey, x);
            }
        }

        public boolean[] getBooleanArray(Bundle state, String key) {
            return state.getBooleanArray(key + baseKey);
        }

        public void putBooleanArray(Bundle state, String key, boolean[] x) {
            state.putBooleanArray(key + baseKey, x);
        }

        public byte getByte(Bundle state, String key) {
            return state.getByte(key + baseKey);
        }

        public void putByte(Bundle state, String key, byte x) {
            state.putByte(key + baseKey, x);
        }

        public Byte getBoxedByte(Bundle state, String key) {
            if (state.containsKey(key + baseKey)) {
                return state.getByte(key + baseKey);
            }
            return null;
        }

        public void putBoxedByte(Bundle state, String key, Byte x) {
            if (x != null) {
                state.putByte(key + baseKey, x);
            }
        }

        public byte[] getByteArray(Bundle state, String key) {
            return state.getByteArray(key + baseKey);
        }

        public void putByteArray(Bundle state, String key, byte[] x) {
            state.putByteArray(key + baseKey, x);
        }


        public short getShort(Bundle state, String key) {
            return state.getShort(key + baseKey);
        }

        public void putShort(Bundle state, String key, short x) {
            state.putShort(key + baseKey, x);
        }

        public Short getBoxedShort(Bundle state, String key) {
            if (state.containsKey(key + baseKey)) {
                return state.getShort(key + baseKey);
            }
            return null;
        }

        public void putBoxedShort(Bundle state, String key, Short x) {
            if (x != null) {
                state.putShort(key + baseKey, x);
            }
        }

        public short[] getShortArray(Bundle state, String key) {
            return state.getShortArray(key + baseKey);
        }

        public void putShortArray(Bundle state, String key, short[] x) {
            state.putShortArray(key + baseKey, x);
        }

        public int getInt(Bundle state, String key) {
            return state.getInt(key + baseKey);
        }

        public void putInt(Bundle state, String key, int x) {
            state.putInt(key + baseKey, x);
        }

        public Integer getBoxedInt(Bundle state, String key) {
            if (state.containsKey(key + baseKey)) {
                return state.getInt(key + baseKey);
            }
            return null;
        }

        public void putBoxedInt(Bundle state, String key, Integer x) {
            if (x != null) {
                state.putInt(key + baseKey, x);
            }
        }

        public int[] getIntArray(Bundle state, String key) {
            return state.getIntArray(key + baseKey);
        }

        public void putIntArray(Bundle state, String key, int[] x) {
            state.putIntArray(key + baseKey, x);
        }

        public long getLong(Bundle state, String key) {
            return state.getLong(key + baseKey);
        }

        public void putLong(Bundle state, String key, long x) {
            state.putLong(key + baseKey, x);
        }

        public Long getBoxedLong(Bundle state, String key) {
            if (state.containsKey(key + baseKey)) {
                return state.getLong(key + baseKey);
            }
            return null;
        }

        public void putBoxedLong(Bundle state, String key, Long x) {
            if (x != null) {
                state.putLong(key + baseKey, x);
            }
        }

        public long[] getLongArray(Bundle state, String key) {
            return state.getLongArray(key + baseKey);
        }

        public void putLongArray(Bundle state, String key, long[] x) {
            state.putLongArray(key + baseKey, x);
        }

        public float getFloat(Bundle state, String key) {
            return state.getFloat(key + baseKey);
        }

        public void putFloat(Bundle state, String key, float x) {
            state.putFloat(key + baseKey, x);
        }

        public Float getBoxedFloat(Bundle state, String key) {
            if (state.containsKey(key + baseKey)) {
                return state.getFloat(key + baseKey);
            }
            return null;
        }

        public void putBoxedFloat(Bundle state, String key, Float x) {
            if (x != null) {
                state.putFloat(key + baseKey, x);
            }
        }

        public float[] getFloatArray(Bundle state, String key) {
            return state.getFloatArray(key + baseKey);
        }

        public void putFloatArray(Bundle state, String key, float[] x) {
            state.putFloatArray(key + baseKey, x);
        }

        public double getDouble(Bundle state, String key) {
            return state.getDouble(key + baseKey);
        }

        public void putDouble(Bundle state, String key, double x) {
            state.putDouble(key + baseKey, x);
        }

        public Double getBoxedDouble(Bundle state, String key) {
            if (state.containsKey(key + baseKey)) {
                return state.getDouble(key + baseKey);
            }
            return null;
        }

        public void putBoxedDouble(Bundle state, String key, Double x) {
            if (x != null) {
                state.putDouble(key + baseKey, x);
            }
        }

        public double[] getDoubleArray(Bundle state, String key) {
            return state.getDoubleArray(key + baseKey);
        }

        public void putDoubleArray(Bundle state, String key, double[] x) {
            state.putDoubleArray(key + baseKey, x);
        }

        public char getChar(Bundle state, String key) {
            return state.getChar(key + baseKey);
        }

        public void putChar(Bundle state, String key, char x) {
            state.putChar(key + baseKey, x);
        }

        public Character getBoxedChar(Bundle state, String key) {
            if (state.containsKey(key + baseKey)) {
                return state.getChar(key + baseKey);
            }
            return null;
        }

        public void putBoxedChar(Bundle state, String key, Character x) {
            if (x != null) {
                state.putChar(key + baseKey, x);
            }
        }

        public char[] getCharArray(Bundle state, String key) {
            return state.getCharArray(key + baseKey);
        }

        public void putCharArray(Bundle state, String key, char[] x) {
            state.putCharArray(key + baseKey, x);
        }

        public String getString(Bundle state, String key) {
            return state.getString(key + baseKey);
        }

        public void putString(Bundle state, String key, String x) {
            state.putString(key + baseKey, x);
        }

        public String[] getStringArray(Bundle state, String key) {
            return state.getStringArray(key + baseKey);
        }

        public void putStringArray(Bundle state, String key, String[] x) {
            state.putStringArray(key + baseKey, x);
        }

        public CharSequence getCharSequence(Bundle state, String key) {
            return state.getCharSequence(key + baseKey);
        }

        public void putCharSequence(Bundle state, String key, CharSequence x) {
            state.putCharSequence(key + baseKey, x);
        }

        public CharSequence[] getCharSequenceArray(Bundle state, String key) {
            return state.getCharSequenceArray(key + baseKey);
        }

        public void putCharSequenceArray(Bundle state, String key, CharSequence[] x) {
            state.putCharSequenceArray(key + baseKey, x);
        }

        public Bundle getBundle(Bundle state, String key) {
            return state.getBundle(key + baseKey);
        }

        public void putBundle(Bundle state, String key, Bundle x) {
            state.putBundle(key + baseKey, x);
        }

        public <T extends Parcelable> T getParcelable(Bundle state, String key) {
            return state.getParcelable(key + baseKey);
        }

        public void putParcelable(Bundle state, String key, Parcelable x) {
            state.putParcelable(key + baseKey, x);
        }

        public Parcelable[] getParcelableArray(Bundle state, String key) {
            return state.getParcelableArray(key + baseKey);
        }

        public void putParcelableArray(Bundle state, String key, Parcelable[] x) {
            state.putParcelableArray(key + baseKey, x);
        }

        public <T extends Serializable> T getSerializable(Bundle state, String key) {
            return (T) state.getSerializable(key + baseKey);
        }

        public void putSerializable(Bundle state, String key, Serializable x) {
            state.putSerializable(key + baseKey, x);
        }

        public ArrayList<Integer> getIntegerArrayList(Bundle state, String key) {
            return state.getIntegerArrayList(key + baseKey);
        }

        public void putIntegerArrayList(Bundle state, String key, ArrayList<Integer> x) {
            state.putIntegerArrayList(key + baseKey, x);
        }

        public ArrayList<String> getStringArrayList(Bundle state, String key) {
            return state.getStringArrayList(key + baseKey);
        }

        public void putStringArrayList(Bundle state, String key, ArrayList<String> x) {
            state.putStringArrayList(key + baseKey, x);
        }

        public ArrayList<CharSequence> getCharSequenceArrayList(Bundle state, String key) {
            return state.getCharSequenceArrayList(key + baseKey);
        }

        public void putCharSequenceArrayList(Bundle state, String key, ArrayList<CharSequence> x) {
            state.putCharSequenceArrayList(key + baseKey, x);
        }

        public <T extends Parcelable> ArrayList<T> getParcelableArrayList(Bundle state, String key) {
            return state.getParcelableArrayList(key + baseKey);
        }

        public void putParcelableArrayList(Bundle state, String key, ArrayList<? extends Parcelable> x) {
            state.putParcelableArrayList(key + baseKey, x);
        }

        public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(Bundle state, String key) {
            return state.getSparseParcelableArray(key + baseKey);
        }

        public void putSparseParcelableArray(Bundle state, String key, SparseArray<? extends Parcelable> x) {
            state.putSparseParcelableArray(key + baseKey, x);
        }

        public Parcelable getParent(Bundle state) {
            return state.getParcelable(baseKey + "$$SUPER");
        }

        public Bundle putParent(Parcelable superState) {
            Bundle state = new Bundle();
            state.putParcelable(baseKey + "$$SUPER", superState);
            return state;
        }
    }

    public static class Object<T> extends Injector {

        public void restore(T target, Bundle state) {
        }

        public void save(T target, Bundle state) {
        }
    }

    public static class View<T> extends Injector {

        public Parcelable restore(T target, Parcelable state) {
            return state;
        }

        public Parcelable save(T target, Parcelable state) {
            return state;
        }
    }
}
