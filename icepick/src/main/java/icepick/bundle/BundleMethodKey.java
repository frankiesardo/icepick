package icepick.bundle;

class BundleMethodKey {

    public final Class<?> clazz;
    public final BundleAction bundleAction;

    public BundleMethodKey(Class<?> clazz, BundleAction bundleAction) {
        this.clazz = clazz;
        this.bundleAction = bundleAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BundleMethodKey that = (BundleMethodKey) o;

        if (bundleAction != that.bundleAction) return false;
        if (clazz != null ? !clazz.equals(that.clazz) : that.clazz != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clazz != null ? clazz.hashCode() : 0;
        result = 31 * result + (bundleAction != null ? bundleAction.hashCode() : 0);
        return result;
    }
}
