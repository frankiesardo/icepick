package icepick.processor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by uniqa on 28.10.14.
 */
class NullSafetyDetector {
    private static final Set<TypeKind> PRIMITIVES = EnumSet.of(
            TypeKind.BOOLEAN,
            TypeKind.BYTE,
            TypeKind.SHORT,
            TypeKind.INT,
            TypeKind.LONG,
            TypeKind.CHAR,
            TypeKind.FLOAT,
            TypeKind.DOUBLE
    );

    private static final Set<TypeMirror> wrappers = new HashSet<TypeMirror>();

    private final Types typeUtils;

    public NullSafetyDetector(Types typeUtils) {
        this.typeUtils = typeUtils;

        for (TypeKind primitive : PRIMITIVES)
            wrappers.add(typeUtils.boxedClass(typeUtils.getPrimitiveType(primitive)).asType());
    }

    private boolean isPrimitiveWrapper(TypeMirror typeMirror) {
        for (TypeMirror wrapper:wrappers)
            if (typeUtils.isSameType(typeMirror, wrapper)) return true;

        return false;
    }

    public boolean isNullable(Element fieldElement) {
        for (AnnotationMirror am:fieldElement.getAnnotationMirrors()) {
            final String annotationName = am.getAnnotationType().asElement().getSimpleName().toString();
            if ("Nullable".equals(annotationName)) return true;
            else if ("NotNull".equals(annotationName) || "Nonnull".equals(annotationName)) return false;
        }

        // Primitive wrappers are stored as their unboxed counterparts, which earns them special treatment
        return isPrimitiveWrapper(fieldElement.asType());
    }
}
