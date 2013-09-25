package icepick.annotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Collection;

class IcicleEnclosingClass {

    public final TypeElement type;
    public final String className;
    public final String packageName;
    public final String parentFqcn;

    IcicleEnclosingClass(TypeElement type, String className, String packageName, String parentFqcn) {
        this.type = type;
        this.className = className;
        this.packageName = packageName;
        this.parentFqcn = parentFqcn;
    }

    static IcicleEnclosingClass newInstance(TypeElement classType, Collection<TypeMirror> parents, Types typeUtils) {
        String className = findName(classType);
        String packageName = findPackage(classType);
        String parentFqcn = findParentFqcn(classType, parents, typeUtils);
        return new IcicleEnclosingClass(classType, className, packageName, parentFqcn);
    }

    private static String findName(TypeElement classType) {
        return classType.getSimpleName().toString();
    }

    private static String findPackage(TypeElement classType) {
        return classType.getQualifiedName().toString().replace("." + classType.getSimpleName(), "");
    }

    private static String findParentFqcn(TypeElement classType, Collection<TypeMirror> parents, Types typeUtils) {
        TypeMirror type;
        while (true) {
            type = classType.getSuperclass();
            if (type.getKind() == TypeKind.NONE) {
                return null;
            }
            classType = (TypeElement) ((DeclaredType) type).asElement();
            if (containsTypeMirror(parents, type, typeUtils)) {
                return classType.getQualifiedName().toString();
            }
        }
    }

    private static boolean containsTypeMirror(Collection<TypeMirror> mirrors, TypeMirror query, Types typeUtils) {
        // Ensure we are checking against a type-erased version for normalization purposes.
        TypeMirror erasure = typeUtils.erasure(query);
        for (TypeMirror mirror : mirrors) {
            if (typeUtils.isSameType(mirror, erasure)) {
                return true;
            }
        }
        return false;
    }
}
