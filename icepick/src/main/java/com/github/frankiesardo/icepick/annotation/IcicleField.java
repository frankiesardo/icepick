package com.github.frankiesardo.icepick.annotation;

import java.util.HashSet;
import java.util.Set;

import static com.github.frankiesardo.icepick.annotation.IcicleCommand.*;

class IcicleField {

    static final Set<String> TYPE_CAST_COMMANDS = new HashSet<String>();

    static {
        TYPE_CAST_COMMANDS.add(INTEGER_ARRAY_LIST);
        TYPE_CAST_COMMANDS.add(STRING_ARRAY_LIST);
        TYPE_CAST_COMMANDS.add(CHAR_SEQUENCE_ARRAY_LIST);
        TYPE_CAST_COMMANDS.add(PARCELABLE_ARRAY_LIST);
        TYPE_CAST_COMMANDS.add(SPARSE_PARCELABLE_ARRAY);

        TYPE_CAST_COMMANDS.add(CHAR_SEQUENCE);
        TYPE_CAST_COMMANDS.add(CHAR_SEQUENCE_ARRAY);
        TYPE_CAST_COMMANDS.add(PARCELABLE);
        TYPE_CAST_COMMANDS.add(PARCELABLE_ARRAY);
        TYPE_CAST_COMMANDS.add(SERIALIZABLE);
    }

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
