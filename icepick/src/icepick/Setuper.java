package icepick;

public interface Setuper<T, F> {

    void setup(T target, F value);
}