package icepick.bundle;

enum BundleAction {
    SAVE("saveInstanceState"),
    RESTORE("restoreInstanceState");

    public final String name;

    BundleAction(String name) {
        this.name = name;
    }
}