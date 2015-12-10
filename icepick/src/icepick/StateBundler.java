package icepick;

import android.os.Bundle;

public interface StateBundler<T> {

    public void put(String key, T value, Bundle bundle);

    public <V extends T> V get(String key, Bundle bundle);

}
