package com.github.frankiesardo.icepick.annotation;

import com.squareup.java.JavaWriter;

import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Set;

class IcicleWriter {

    private final JavaWriter javaWriter;
    private final String suffix;

    public IcicleWriter(JavaWriter javaWriter, String suffix) {
        this.javaWriter = javaWriter;
        this.suffix = suffix;
    }

    public void writeClass(TypeElement classType, Set<IcicleField> fields) throws IOException {
        writePackage(classType);
        writeType(classType);
        writeConstructor(classType);
        writeOnSaveInstanceState(classType, fields);
        writeOnRestoreInstanceState(classType, fields);
        writeEnd();
    }

    private void writePackage(TypeElement classType) throws IOException {
        javaWriter.emitPackage(classType.getQualifiedName().toString().replace("." + classType.getSimpleName(), ""));
    }

    private JavaWriter writeType(TypeElement classType) throws IOException {
        return javaWriter.beginType(classType.getQualifiedName() + suffix, "class", Modifier.FINAL);
    }

    private void writeConstructor(TypeElement typeElement) throws IOException {
        javaWriter.beginMethod(null, typeElement.getSimpleName().toString() + suffix, Modifier.PRIVATE);
        javaWriter.endMethod();
    }

    private void writeOnRestoreInstanceState(TypeElement classType, Set<IcicleField> fields) throws IOException {
        javaWriter.beginMethod("void", "restoreInstanceState", Modifier.PUBLIC | Modifier.STATIC, classType.getQualifiedName().toString(), "target", "android.os.Bundle", "savedInstanceState");
        javaWriter.beginControlFlow("if (savedInstanceState == null)");
        javaWriter.emitStatement("return");
        javaWriter.endControlFlow();
        for (IcicleField field : fields) {
            writeBundleGet(field);
        }
        javaWriter.endMethod();
    }

    private void writeBundleGet(IcicleField icicleField) throws IOException {
        javaWriter.emitStatement("target.%1$s = %2$ssavedInstanceState.get%3$s(\"%4$s\")", icicleField.getName(), icicleField.getTypeCast(), icicleField.getCommand(), icicleField.getKey());
    }

    private void writeOnSaveInstanceState(TypeElement classType, Set<IcicleField> fields) throws IOException {
        javaWriter.beginMethod("void", "saveInstanceState", Modifier.PUBLIC | Modifier.STATIC, classType.getQualifiedName().toString(), "target", "android.os.Bundle", "outState");
        for (IcicleField field : fields) {
            writeBundlePut(field);
        }
        javaWriter.endMethod();
    }

    private void writeBundlePut(IcicleField icicleField) throws IOException {
        javaWriter.emitStatement("outState.put%1$s(\"%2$s\", target.%3$s)", icicleField.getCommand(), icicleField.getKey(), icicleField.getName());
    }

    private void writeEnd() throws IOException {
        javaWriter.endType();
        javaWriter.close();
    }
}
