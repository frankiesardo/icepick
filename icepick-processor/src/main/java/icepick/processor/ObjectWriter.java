package icepick.processor;

import com.google.common.base.Joiner;
import javax.tools.JavaFileObject;

class ObjectWriter extends AbsWriter {

  ObjectWriter(JavaFileObject jfo, String suffix, EnclosingClass enclosingClass) {
    super(jfo, suffix, enclosingClass);
  }

  @Override protected String getWriterType() {
    return "Bundle";
  }

  @Override protected String emitRestoreStateStart() {
    return Joiner.on("\n").join(
        "    if (state == null) {",
        "      return null;",
        "    }",
        "    Bundle savedInstanceState = state;"
    );
  }

  @Override protected String emitRestoreStateEnd() {
    return "    return parent.restoreInstanceState(target, savedInstanceState);";
  }

  @Override protected String emitSaveStateStart() {
    return Joiner.on("\n").join(
        "    parent.saveInstanceState(target, state);" ,
        "    Bundle outState = state;"
    );
  }

  @Override protected String emitSaveStateEnd() {
    return "    return outState;";
  }
}
