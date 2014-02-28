package icepick.processor;

import com.google.common.base.Joiner;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import javax.tools.JavaFileObject;

abstract class AbsWriter {

  protected static final String BASE_KEY = "BASE_KEY";

  private final JavaFileObject javaFileObject;
  private final String suffix;
  private final EnclosingClass enclosingClass;

  public AbsWriter(JavaFileObject jfo, String suffix, EnclosingClass enclosingClass) {
    this.javaFileObject = jfo;
    this.suffix = suffix;
    this.enclosingClass = enclosingClass;
  }

  public void withFields(Collection<AnnotatedField> annotatedFields) throws IOException {
    Writer writer = javaFileObject.openWriter();
    writer.write(brewJava(annotatedFields));
    writer.flush();
    writer.close();
  }

  private String brewJava(Collection<AnnotatedField> annotatedFields) {
    String classPackage = enclosingClass.getClassPackage();
    String helperClassName = enclosingClass.getClassName() + suffix;
    String fqHelperClassName = classPackage + "." + helperClassName;

    String type = getWriterType();
    String helperType = "StateHelper<" + type + ">";

    String parentClass = enclosingClass.getParentEnclosingClass() == null
        ? "(" + helperType + ") StateHelper.NO_OP;"
        : "new " + enclosingClass.getParentEnclosingClass() + suffix + "();";
    String targetClass = enclosingClass.getTargetClass();

    return Joiner.on("\n").join(
        "// Generated code from Icepick. Do not modify!",
        "package " + classPackage + ";",
        "import android.os.Bundle;",
        "import android.os.Parcelable;",
        "import icepick.StateHelper;",
        "public class " + helperClassName + " implements " + helperType + " {",
        "",
        "  private static final String " + BASE_KEY + " = \"" + fqHelperClassName + ".\";",
        "  private final " + helperType + " parent = " + parentClass,
        "",
        "  public " + type + " restoreInstanceState(Object obj, " + type + " state) {",
        "    " + targetClass + " target = (" + targetClass + ") obj;",
        emitRestoreStateStart(),
        "",
        emitFieldRestoreState(annotatedFields),
        emitRestoreStateEnd(),
        "  }",
        "  public " + type + " saveInstanceState(Object obj, " + type + " state) {",
        "    " + targetClass + " target = (" + targetClass + ") obj;",
        emitSaveStateStart(),
        "",
        emitFieldSaveState(annotatedFields),
        emitSaveStateEnd(),
        "  }",
        "}"
        );
  }

  protected abstract String getWriterType();

  protected abstract String emitRestoreStateStart();

  private String emitFieldRestoreState(Collection<AnnotatedField> annotatedFields) {
    StringBuilder builder = new StringBuilder();
    for (AnnotatedField field : annotatedFields) {
      builder.append(emitRestoreState(field));
    }
    return builder.toString();
  }

  private String emitRestoreState(AnnotatedField field) {
    return "    target." + field.getName() + " = " + field.getTypeCast() + " savedInstanceState.get"
        + field.getBundleMethod() + "(" + BASE_KEY + " + \"" + field.getName() + "\");\n";
  }

  protected abstract String emitRestoreStateEnd();

  protected abstract String emitSaveStateStart();

  private String emitFieldSaveState(Collection<AnnotatedField> annotatedFields) {
    StringBuilder builder = new StringBuilder();
    for (AnnotatedField field : annotatedFields) {
      builder.append(emitSaveState(field));
    }
    return builder.toString();
  }

  private String emitSaveState(AnnotatedField field) {
    return "    outState.put" + field.getBundleMethod() + "(" + BASE_KEY + " + \""
        + field.getName() + "\", target." + field.getName() + ");\n";
  }

  protected abstract String emitSaveStateEnd();
}
