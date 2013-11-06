package icepick.annotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class IcicleConversionMap extends LinkedHashMap<TypeMirror, String> {

    private final Elements elementUtils;
    private final Types typeUtils;

    public IcicleConversionMap(Elements elementUtils, Types typeUtils) {
        this.elementUtils = elementUtils;
        this.typeUtils = typeUtils;

        for (String type : DICTIONARY.keySet()) {
            put(getMirror(type), DICTIONARY.get(type));
        }
    }

    private TypeMirror getMirror(String type) {
        if (isCollection(type)) {
            return getCollectionMirror(type);
        }
        if (isArray(type)) {
            return getArrayMirror(type);
        }
        if (isPrimitive(type)) {
            return getPrimitiveMirror(type);
        }
        return getSimpleMirror(type);
    }

    private boolean isCollection(String type) {
        return type.indexOf('<') != -1;
    }

    private TypeMirror getCollectionMirror(String type) {
        String collectionType = type.substring(0, type.indexOf('<'));
        String elementType = type.substring(type.indexOf('<') + 1, type.length() - 1);

        TypeElement collectionTypeElement = elementUtils.getTypeElement(collectionType);

        TypeMirror elementTypeMirror = getElementMirror(elementType);

        return typeUtils.getDeclaredType(collectionTypeElement, elementTypeMirror);
    }

    private TypeMirror getElementMirror(String elementType) {
        if (isWildCard(elementType)) {
            TypeElement typeElement = elementUtils.getTypeElement(wildCard(elementType));
            return typeUtils.getWildcardType(typeElement.asType(), null);
        }
        return elementUtils.getTypeElement(elementType).asType();
    }

    private boolean isWildCard(String elementType) {
        return elementType.startsWith("?");
    }

    private String wildCard(String elementType) {
        return elementType.substring("? extends ".length());
    }

    private boolean isArray(String type) {
        return type.endsWith("[]");
    }

    private ArrayType getArrayMirror(String type) {
        return typeUtils.getArrayType(getMirror(type.substring(0, type.length() - 2)));
    }

    private boolean isPrimitive(String type) {
        return type.indexOf('.') == -1;
    }

    private PrimitiveType getPrimitiveMirror(String type) {
        return typeUtils.getPrimitiveType(TypeKind.valueOf(type.toUpperCase()));
    }

    private TypeMirror getSimpleMirror(String type) {
        return elementUtils.getTypeElement(type).asType();
    }

    static final String STRING = "String";
    static final String STRING_ARRAY = "StringArray";
    static final String INT = "Int";
    static final String INT_ARRAY = "IntArray";
    static final String LONG = "Long";
    static final String LONG_ARRAY = "LongArray";
    static final String DOUBLE = "Double";
    static final String DOUBLE_ARRAY = "DoubleArray";
    static final String SHORT = "Short";
    static final String SHORT_ARRAY = "ShortArray";
    static final String FLOAT = "Float";
    static final String FLOAT_ARRAY = "FloatArray";
    static final String BYTE = "Byte";
    static final String BYTE_ARRAY = "ByteArray";
    static final String BOOLEAN = "Boolean";
    static final String BOOLEAN_ARRAY = "BooleanArray";
    static final String CHAR = "Char";
    static final String CHAR_ARRAY = "CharArray";
    static final String CHAR_SEQUENCE = "CharSequence";
    static final String CHAR_SEQUENCE_ARRAY = "CharSequenceArray";
    static final String BUNDLE = "Bundle";
    static final String BUNDLE_ARRAY = "BundleArray";
    static final String PARCELABLE = "Parcelable";
    static final String PARCELABLE_ARRAY = "ParcelableArray";

    static final String SERIALIZABLE = "Serializable";

    static final String PARCELABLE_ARRAY_LIST = "ParcelableArrayList";
    static final String SPARSE_PARCELABLE_ARRAY = "SparseParcelableArray";
    static final String STRING_ARRAY_LIST = "StringArrayList";
    static final String INTEGER_ARRAY_LIST = "IntegerArrayList";
    static final String CHAR_SEQUENCE_ARRAY_LIST = "CharSequenceArrayList";

    static final Map<String, String> DICTIONARY = new LinkedHashMap<String, String>();

    static {

        DICTIONARY.put("java.util.ArrayList<java.lang.Integer>", INTEGER_ARRAY_LIST);
        DICTIONARY.put("java.util.ArrayList<java.lang.String>", STRING_ARRAY_LIST);
        DICTIONARY.put("java.util.ArrayList<java.lang.CharSequence>", CHAR_SEQUENCE_ARRAY_LIST);

        DICTIONARY.put("java.util.ArrayList<? extends android.os.Parcelable>", PARCELABLE_ARRAY_LIST);
        DICTIONARY.put("android.util.SparseArray<? extends android.os.Parcelable>", SPARSE_PARCELABLE_ARRAY);

        DICTIONARY.put("java.lang.String", STRING);
        DICTIONARY.put("java.lang.String[]", STRING_ARRAY);
        DICTIONARY.put("int", INT);
        DICTIONARY.put("int[]", INT_ARRAY);
        DICTIONARY.put("java.lang.Integer", INT);
        DICTIONARY.put("java.lang.Integer[]", INT_ARRAY);
        DICTIONARY.put("long", LONG);
        DICTIONARY.put("long[]", LONG_ARRAY);
        DICTIONARY.put("java.lang.Long", LONG);
        DICTIONARY.put("java.lang.Long[]", LONG_ARRAY);
        DICTIONARY.put("double", DOUBLE);
        DICTIONARY.put("double[]", DOUBLE_ARRAY);
        DICTIONARY.put("java.lang.Double", DOUBLE);
        DICTIONARY.put("java.lang.Double[]", DOUBLE_ARRAY);
        DICTIONARY.put("short", SHORT);
        DICTIONARY.put("short[]", SHORT_ARRAY);
        DICTIONARY.put("java.lang.Short", SHORT);
        DICTIONARY.put("java.lang.Short[]", SHORT_ARRAY);
        DICTIONARY.put("float", FLOAT);
        DICTIONARY.put("float[]", FLOAT_ARRAY);
        DICTIONARY.put("java.lang.Float", FLOAT);
        DICTIONARY.put("java.lang.Float[]", FLOAT_ARRAY);
        DICTIONARY.put("byte", BYTE);
        DICTIONARY.put("byte[]", BYTE_ARRAY);
        DICTIONARY.put("java.lang.Byte", BYTE);
        DICTIONARY.put("java.lang.Byte[]", BYTE_ARRAY);
        DICTIONARY.put("boolean", BOOLEAN);
        DICTIONARY.put("boolean[]", BOOLEAN_ARRAY);
        DICTIONARY.put("java.lang.Boolean", BOOLEAN);
        DICTIONARY.put("java.lang.Boolean[]", BOOLEAN_ARRAY);
        DICTIONARY.put("char", CHAR);
        DICTIONARY.put("char[]", CHAR_ARRAY);
        DICTIONARY.put("java.lang.Character", CHAR);
        DICTIONARY.put("java.lang.Character[]", CHAR_ARRAY);
        DICTIONARY.put("java.lang.CharSequence", CHAR_SEQUENCE);
        DICTIONARY.put("android.os.Bundle", BUNDLE);
        DICTIONARY.put("android.os.Bundle[]", BUNDLE_ARRAY);

        DICTIONARY.put("java.lang.CharSequence[]", CHAR_SEQUENCE_ARRAY);
        DICTIONARY.put("android.os.Parcelable", PARCELABLE);
        DICTIONARY.put("android.os.Parcelable[]", PARCELABLE_ARRAY);
        DICTIONARY.put("java.io.Serializable", SERIALIZABLE);
    }

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
}
