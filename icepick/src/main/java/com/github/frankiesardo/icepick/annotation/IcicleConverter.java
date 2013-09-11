package com.github.frankiesardo.icepick.annotation;

import java.util.HashMap;
import java.util.Map;

import static com.github.frankiesardo.icepick.annotation.IcicleCommand.*;

class IcicleConverter {

    private static final Map<String, String> RAW_TYPES = new HashMap<String, String>();

    static {
        RAW_TYPES.put("java.lang.String", STRING);
        RAW_TYPES.put("java.lang.String[]", STRING_ARRAY);
        RAW_TYPES.put("int", INT);
        RAW_TYPES.put("int[]", INT_ARRAY);
        RAW_TYPES.put("java.lang.Integer", INT);
        RAW_TYPES.put("java.lang.Integer[]", INT_ARRAY);
        RAW_TYPES.put("long", LONG);
        RAW_TYPES.put("long[]", LONG_ARRAY);
        RAW_TYPES.put("java.lang.Long", LONG);
        RAW_TYPES.put("java.lang.Long[]", LONG_ARRAY);
        RAW_TYPES.put("double", DOUBLE);
        RAW_TYPES.put("double[]", DOUBLE_ARRAY);
        RAW_TYPES.put("java.lang.Double", DOUBLE);
        RAW_TYPES.put("java.lang.Double[]", DOUBLE_ARRAY);
        RAW_TYPES.put("short", SHORT);
        RAW_TYPES.put("short[]", SHORT_ARRAY);
        RAW_TYPES.put("java.lang.Short", SHORT);
        RAW_TYPES.put("java.lang.Short[]", SHORT_ARRAY);
        RAW_TYPES.put("float", FLOAT);
        RAW_TYPES.put("float[]", FLOAT_ARRAY);
        RAW_TYPES.put("java.lang.Float", FLOAT);
        RAW_TYPES.put("java.lang.Float[]", FLOAT_ARRAY);
        RAW_TYPES.put("byte", BYTE);
        RAW_TYPES.put("byte[]", BYTE_ARRAY);
        RAW_TYPES.put("java.lang.Byte", BYTE);
        RAW_TYPES.put("java.lang.Byte[]", BYTE_ARRAY);
        RAW_TYPES.put("boolean", BOOLEAN);
        RAW_TYPES.put("boolean[]", BOOLEAN_ARRAY);
        RAW_TYPES.put("java.lang.Boolean", BOOLEAN);
        RAW_TYPES.put("java.lang.Boolean[]", BOOLEAN_ARRAY);
        RAW_TYPES.put("char", CHAR);
        RAW_TYPES.put("char[]", CHAR_ARRAY);
        RAW_TYPES.put("java.lang.Character", CHAR);
        RAW_TYPES.put("java.lang.Character[]", CHAR_ARRAY);
        RAW_TYPES.put("java.lang.CharSequence", CHAR_SEQUENCE);
        RAW_TYPES.put("java.lang.CharSequence[]", CHAR_SEQUENCE_ARRAY);

        RAW_TYPES.put("android.os.Bundle", BUNDLE);
        RAW_TYPES.put("android.os.Bundle[]", BUNDLE_ARRAY);
        RAW_TYPES.put("android.os.Parcelable[]", PARCELABLE_ARRAY);

        RAW_TYPES.put("java.util.ArrayList<java.lang.String>", STRING_ARRAY_LIST);
        RAW_TYPES.put("java.util.ArrayList<java.lang.CharSequence>", CHAR_SEQUENCE_ARRAY_LIST);
        RAW_TYPES.put("java.util.ArrayList<java.lang.Integer>", INTEGER_ARRAY_LIST);

    }

    private final IcicleAssigner assigner;

    public IcicleConverter(IcicleAssigner assigner) {
        this.assigner = assigner;
    }

    public String convert(String typeMirror) {
        String type = asRawTpe(typeMirror);
        if (type != null) {
            return type;
        }

        type = asImplementedInterface(typeMirror);
        if (type != null) {
            return type;
        }

        type = asParcelableCollection(typeMirror);
        if (type != null) {
            return type;
        }

        throw new RuntimeException("Impossible to put a " + typeMirror + " into a Bundle");
    }

    private String asRawTpe(String typeMirror) {
        return RAW_TYPES.get(typeMirror);
    }

    private String asImplementedInterface(String typeMirror) {
        if (isParcelable(typeMirror)) {
            return PARCELABLE;
        }
        if (isSerializable(typeMirror)) {
            return SERIALIZABLE;
        }
        return null;
    }

    private boolean isParcelable(String typeMirror) {
        return isAssignable(typeMirror, "android.os.Parcelable");
    }

    private boolean isSerializable(String typeMirror) {
        if (isGenericType(typeMirror)) {
            return isAssignable(getGenericType(typeMirror), "java.io.Serializable") && isSerializable(getGenericArgument(typeMirror));
        }
        return isAssignable(typeMirror, "java.io.Serializable");
    }

    private boolean isGenericType(String typeMirror) {
        return typeMirror.indexOf('<') != -1;
    }

    private String getGenericType(String typeMirror) {
        return typeMirror.substring(0, typeMirror.indexOf('<'));
    }

    private String getGenericArgument(String typeMirror) {
        return typeMirror.substring(typeMirror.indexOf('<') + 1, typeMirror.length() - 1);
    }

    private boolean isAssignable(String typeMirror, String typeName) {
        return assigner.isAssignable(typeMirror, typeName);
    }

    private String asParcelableCollection(String typeMirror) {
        if (isParcelableArrayList(typeMirror)) {
            return PARCELABLE_ARRAY_LIST;
        }
        if (isSparseParcelableArray(typeMirror)) {
            return SPARSE_PARCELABLE_ARRAY;
        }
        return null;
    }

    private boolean isParcelableArrayList(String typeMirror) {
        return isArrayList(typeMirror) && isParcelable(getArrayListType(typeMirror));
    }

    private boolean isArrayList(String typeMirror) {
        return typeMirror.startsWith("java.util.ArrayList");
    }

    private String getArrayListType(String typeMirror) {
        return typeMirror.substring(20, typeMirror.length() - 1);
    }

    private boolean isSparseParcelableArray(String typeMirror) {
        return isSparseArray(typeMirror) && isParcelable(getSparseArrayType(typeMirror));
    }

    private boolean isSparseArray(String typeMirror) {
        return typeMirror.startsWith("android.util.SparseArray");
    }

    private String getSparseArrayType(String typeMirror) {
        return typeMirror.substring(25, typeMirror.length() - 1);
    }
}
