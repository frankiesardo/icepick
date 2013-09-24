package com.github.frankiesardo.icepick.annotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.LinkedHashMap;
import java.util.Map;

class IcicleConverter {

    private final Map<TypeMirror, String> conversionMap = new LinkedHashMap<TypeMirror, String>();

    private final Elements elementUtils;
    private final Types typeUtils;

    public IcicleConverter(Elements elementUtils, Types typeUtils) {
        this.elementUtils = elementUtils;
        this.typeUtils = typeUtils;

        initMap();
    }

    private void initMap() {
        for (String type : IcicleCommand.DICTIONARY.keySet()) {
            conversionMap.put(getMirror(type), IcicleCommand.DICTIONARY.get(type));
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

    public String convert(TypeMirror typeMirror) {
        for (TypeMirror other : conversionMap.keySet()) {
            if (typeUtils.isAssignable(typeMirror, other)) {
                return conversionMap.get(other);
            }
        }
        throw new AssertionError("Cannot insert a " + typeMirror + " into a Bundle");
    }
}
