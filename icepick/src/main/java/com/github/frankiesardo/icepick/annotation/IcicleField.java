package com.github.frankiesardo.icepick.annotation;

class IcicleField {

    private final String name;
    private final String type;
    private final String command;

    public IcicleField(String name, String type, String command) {
        this.name = name;
        this.command = command;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getTypeCast() {
        if (command.equals(IcicleCommand.SERIALIZABLE) ||
                command.equals(IcicleCommand.PARCELABLE)) {
            return "(" + type + ") ";
        }
        return "";
    }

    public String getCommand() {
        return command;
    }
}
