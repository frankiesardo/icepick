package icepick;

import android.os.Bundle;

public interface Bundler<T> {

    void put(String key, T value, Bundle bundle);

    T get(String key, Bundle bundle);
}