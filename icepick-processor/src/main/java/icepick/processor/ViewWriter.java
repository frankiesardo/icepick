package icepick.processor;

import com.google.common.base.Joiner;
import javax.tools.JavaFileObject;

class ViewWriter extends AbsWriter {

  private static final String SUPER_SUFFIX = "\"$$SUPER$$\"";
  private static final String SUPER_KEY = BASE_KEY + " + " + SUPER_SUFFIX;

  public ViewWriter(JavaFileObject jfo, String suffix, EnclosingClass enclosingClass) {
    super(jfo, suffix, enclosingClass);
  }

  @Override protected String getWriterType() {
    return "Parcelable";
  }

  @Override protected String emitRestoreStateStart() {
    return Joiner.on("\n").join(
        "    Bundle savedInstanceState = (Bundle) state;",
        "    Parcelable superState = savedInstanceState.getParcelable(" + SUPER_KEY + ");"
    );
  }

  @Override protected String emitRestoreStateEnd() {
    return "    return parent.restoreInstanceState(target, superState);";
  }

  @Override protected String emitSaveStateStart() {
    return Joiner.on("\n").join(
        "    Bundle outState = new Bundle();",
        "    Parcelable superState = parent.saveInstanceState(target, state);",
        "    outState.putParcelable(" + SUPER_KEY + ", superState);"
    );
  }

  @Override protected String emitSaveStateEnd() {
    return "    return outState;";
  }
}
