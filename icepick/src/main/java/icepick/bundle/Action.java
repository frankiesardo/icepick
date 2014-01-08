package icepick.bundle;

enum Action {
    SAVE("saveInstanceState"),
    RESTORE("restoreInstanceState");

    public final String name;

    Action(String name) {
        this.name = name;
    }
}