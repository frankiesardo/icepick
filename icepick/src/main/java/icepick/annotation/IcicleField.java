package icepick.annotation;

import static icepick.annotation.IcicleCommand.TYPE_CAST_COMMANDS;

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
        if (TYPE_CAST_COMMANDS.contains(command)) {
            return "(" + type + ") ";
        }
        return "";
    }

    public String getCommand() {
        return command;
    }
}
