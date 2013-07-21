package com.github.frankiesardo.icepick.annotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class IcicleAssigner {

    private final Types typeUtils;
    private final Elements elementUtils;

    public IcicleAssigner(Types typeUtils, Elements elementUtils) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
    }

    public boolean isAssignable(String classNameFrom, String classNameTo) {
        TypeElement typeFrom = elementUtils.getTypeElement(classNameFrom);
        TypeElement typeTo = elementUtils.getTypeElement(classNameTo);
        if (typeFrom == null || typeTo == null) {
            return false;
        }
        return typeUtils.isAssignable(typeFrom.asType(), typeTo.asType());
    }
}
