package com.github.frankiesardo.icepick.annotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.github.frankiesardo.icepick.annotation.IcicleCommand.*;

class IcicleConverter {

    private final Types typeUtils;
    private final Elements elementUtils;

    public IcicleConverter(Types typeUtils, Elements elementUtils) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
    }

    public String convert(TypeMirror typeMirror) {
        String type = asRawTpe(typeMirror);
        if (type != null) {
            return type;
        }

        type = asImplementedInterface(typeMirror);
        if (type != null) {
            return type;
        }

        type = asRawCollection(typeMirror);
        if (type != null) {
            return type;
        }

        type = asParcelableWildcardCollection(typeMirror);
        if (type != null) {
            return type;
        }

        throw new RuntimeException("Impossible to put a " + typeMirror + " into a Bundle");
    }

    private String asRawTpe(TypeMirror typeMirror) {
        return RAW_TYPES.get(typeMirror.toString());
    }

    private String asImplementedInterface(TypeMirror typeMirror) {
        if (isOfType(typeMirror, "android.os.Parcelable")) {
            return PARCELABLE;
        }
        if (isOfType(typeMirror, Serializable.class.getName())) {
            return SERIALIZABLE;
        }
        return null;
    }

    private boolean isOfType(TypeMirror typeMirror, String typeName) {
        return typeUtils.isAssignable(typeMirror, elementUtils.getTypeElement(typeName).asType());
    }

    private String asRawCollection(TypeMirror typeMirror) {
        String[] arrayListTypes = new String[]{String.class.getName(), Integer.class.getName(), CharSequence.class.getName()};
        String[] arrayListOps = new String[]{STRING_ARRAY_LIST, INTEGER_ARRAY_LIST, CHAR_SEQUENCE_ARRAY_LIST};
        for (int i = 0; i < arrayListTypes.length; i++) {
            TypeMirror arrayListType = getArrayListType(arrayListTypes[i]);
            if (typeUtils.isAssignable(typeMirror, arrayListType)) {
                return arrayListOps[i];
            }
        }
        return null;
    }

    private TypeMirror getArrayListType(String elementType) {
        TypeElement arrayList = elementUtils.getTypeElement(ArrayList.class.getName());
        TypeMirror elType = elementUtils.getTypeElement(elementType).asType();
        return typeUtils.getDeclaredType(arrayList, elType);
    }

    private String asParcelableWildcardCollection(TypeMirror typeMirror) {
        if (typeUtils.isAssignable(typeMirror, getBoundedWildcardType(ArrayList.class.getName(), "android.os.Parcelable"))) {
            return PARCELABLE_ARRAY_LIST;
        }
        if (typeUtils.isAssignable(typeMirror, getBoundedWildcardType("android.util.SparseArray", "android.os.Parcelable"))) {
            return SPARSE_PARCELABLE_ARRAY;
        }
        return null;
    }

    private TypeMirror getBoundedWildcardType(String type, String elementType) {
        TypeElement arrayList = elementUtils.getTypeElement(type);
        TypeMirror elType = elementUtils.getTypeElement(elementType).asType();
        return typeUtils.getDeclaredType(arrayList, typeUtils.getWildcardType(elType, null));
    }

    private static final Map<String, String> RAW_TYPES = new HashMap<String, String>();

    static {
        RAW_TYPES.put(String.class.getName(), STRING);
        RAW_TYPES.put(String[].class.getName(), STRING_ARRAY);
        RAW_TYPES.put(int.class.getName(), INT);
        RAW_TYPES.put(int[].class.getName(), INT_ARRAY);
        RAW_TYPES.put(Integer.class.getName(), INT);
        RAW_TYPES.put(Integer[].class.getName(), INT_ARRAY);
        RAW_TYPES.put(long.class.getName(), LONG);
        RAW_TYPES.put(long[].class.getName(), LONG_ARRAY);
        RAW_TYPES.put(Long.class.getName(), LONG);
        RAW_TYPES.put(Long[].class.getName(), LONG_ARRAY);
        RAW_TYPES.put(double.class.getName(), DOUBLE);
        RAW_TYPES.put(double[].class.getName(), DOUBLE_ARRAY);
        RAW_TYPES.put(Double.class.getName(), DOUBLE);
        RAW_TYPES.put(Double[].class.getName(), DOUBLE_ARRAY);
        RAW_TYPES.put(short.class.getName(), SHORT);
        RAW_TYPES.put(short[].class.getName(), SHORT_ARRAY);
        RAW_TYPES.put(Short.class.getName(), SHORT);
        RAW_TYPES.put(Short[].class.getName(), SHORT_ARRAY);
        RAW_TYPES.put(float.class.getName(), FLOAT);
        RAW_TYPES.put(float[].class.getName(), FLOAT_ARRAY);
        RAW_TYPES.put(Float.class.getName(), FLOAT);
        RAW_TYPES.put(Float[].class.getName(), FLOAT_ARRAY);
        RAW_TYPES.put(byte.class.getName(), BYTE);
        RAW_TYPES.put(byte[].class.getName(), BYTE_ARRAY);
        RAW_TYPES.put(Byte.class.getName(), BYTE);
        RAW_TYPES.put(Byte[].class.getName(), BYTE_ARRAY);
        RAW_TYPES.put(boolean.class.getName(), BOOLEAN);
        RAW_TYPES.put(boolean[].class.getName(), BOOLEAN_ARRAY);
        RAW_TYPES.put(Boolean.class.getName(), BOOLEAN);
        RAW_TYPES.put(Boolean[].class.getName(), BOOLEAN_ARRAY);
        RAW_TYPES.put(char.class.getName(), CHAR);
        RAW_TYPES.put(char[].class.getName(), CHAR_ARRAY);
        RAW_TYPES.put(Character.class.getName(), CHAR);
        RAW_TYPES.put(Character[].class.getName(), CHAR_ARRAY);
        RAW_TYPES.put(CharSequence.class.getName(), CHAR_SEQUENCE);
        RAW_TYPES.put(CharSequence[].class.getName(), CHAR_SEQUENCE_ARRAY);
        RAW_TYPES.put("android.os.Bundle", BUNDLE);
        RAW_TYPES.put("android.os.Bundle[]", BUNDLE_ARRAY);
        RAW_TYPES.put("android.os.Parcelable[]", PARCELABLE_ARRAY);
    }
}
