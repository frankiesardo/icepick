package icepick.bundle;

class MethodKey {

    public final Class<?> clazz;
    public final Action action;

    public MethodKey(Class<?> clazz, Action action) {
        this.clazz = clazz;
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodKey that = (MethodKey) o;

        if (action != that.action) return false;
        if (clazz != null ? !clazz.equals(that.clazz) : that.clazz != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clazz != null ? clazz.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }
}
