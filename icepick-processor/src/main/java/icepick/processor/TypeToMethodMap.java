package icepick.processor;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class TypeToMethodMap extends LinkedHashMap<TypeMirror, String> {

  private final Elements elementUtils;
  private final Types typeUtils;

  public TypeToMethodMap(Elements elementUtils, Types typeUtils) {
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;

    for (String type : DICTIONARY.keySet()) {
      put(getMirror(type), DICTIONARY.get(type));
    }
  }

  public boolean requiresTypeCast(String bundleMethod) {
    return REQUIRE_TYPE_CAST_METHODS.contains(bundleMethod);
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

  private static final Map<String, String> DICTIONARY = new LinkedHashMap<String, String>();

  static {

    DICTIONARY.put("java.util.ArrayList<java.lang.Integer>", "IntegerArrayList");
    DICTIONARY.put("java.util.ArrayList<java.lang.String>", "StringArrayList");
    DICTIONARY.put("java.util.ArrayList<java.lang.CharSequence>", "CharSequenceArrayList");

    DICTIONARY.put("java.util.ArrayList<? extends android.os.Parcelable>", "ParcelableArrayList");
    DICTIONARY.put("android.util.SparseArray<? extends android.os.Parcelable>",
        "SparseParcelableArray");

    DICTIONARY.put("short", "Short");
    DICTIONARY.put("short[]", "ShortArray");
    DICTIONARY.put("java.lang.Short", "Short");
    DICTIONARY.put("java.lang.Short[]", "ShortArray");
    DICTIONARY.put("int", "Int");
    DICTIONARY.put("int[]", "IntArray");
    DICTIONARY.put("java.lang.Integer", "Int");
    DICTIONARY.put("java.lang.Integer[]", "IntArray");
    DICTIONARY.put("long", "Long");
    DICTIONARY.put("long[]", "LongArray");
    DICTIONARY.put("java.lang.Long", "Long");
    DICTIONARY.put("java.lang.Long[]", "LongArray");
    DICTIONARY.put("float", "Float");
    DICTIONARY.put("float[]", "FloatArray");
    DICTIONARY.put("java.lang.Float", "Float");
    DICTIONARY.put("java.lang.Float[]", "FloatArray");
    DICTIONARY.put("double", "Double");
    DICTIONARY.put("double[]", "DoubleArray");
    DICTIONARY.put("java.lang.Double", "Double");
    DICTIONARY.put("java.lang.Double[]", "DoubleArray");
    DICTIONARY.put("byte", "Byte");
    DICTIONARY.put("byte[]", "ByteArray");
    DICTIONARY.put("java.lang.Byte", "Byte");
    DICTIONARY.put("java.lang.Byte[]", "ByteArray");
    DICTIONARY.put("boolean", "Boolean");
    DICTIONARY.put("boolean[]", "BooleanArray");
    DICTIONARY.put("java.lang.Boolean", "Boolean");
    DICTIONARY.put("java.lang.Boolean[]", "BooleanArray");
    DICTIONARY.put("char", "Char");
    DICTIONARY.put("char[]", "CharArray");
    DICTIONARY.put("java.lang.Character", "Char");
    DICTIONARY.put("java.lang.Character[]", "CharArray");
    DICTIONARY.put("java.lang.String", "String");
    DICTIONARY.put("java.lang.String[]", "StringArray");
    DICTIONARY.put("android.os.Bundle", "Bundle");

    DICTIONARY.put("java.lang.CharSequence", "CharSequence");
    DICTIONARY.put("java.lang.CharSequence[]", "CharSequenceArray");
    DICTIONARY.put("android.os.Parcelable", "Parcelable");
    DICTIONARY.put("android.os.Parcelable[]", "ParcelableArray");
    DICTIONARY.put("java.io.Serializable", "Serializable");
  }

  private static final Set<String> REQUIRE_TYPE_CAST_METHODS = new HashSet<String>();

  static {
    REQUIRE_TYPE_CAST_METHODS.add("IntegerArrayList");
    REQUIRE_TYPE_CAST_METHODS.add("StringArrayList");
    REQUIRE_TYPE_CAST_METHODS.add("CharSequenceArrayList");
    REQUIRE_TYPE_CAST_METHODS.add("ParcelableArrayList");
    REQUIRE_TYPE_CAST_METHODS.add("SparseParcelableArray");

    REQUIRE_TYPE_CAST_METHODS.add("CharSequence");
    REQUIRE_TYPE_CAST_METHODS.add("CharSequenceArray");
    REQUIRE_TYPE_CAST_METHODS.add("Parcelable");
    REQUIRE_TYPE_CAST_METHODS.add("ParcelableArray");
    REQUIRE_TYPE_CAST_METHODS.add("Serializable");
  }
}