package com.github.frankiesardo.icepick;

import android.os.Parcelable;
import icepick.Icicle;
import java.io.File;

public class AnnotatedObject {

  @Icicle String string;
  @Icicle File file;
  @Icicle Parcelable parcelable;
  @Icicle Object object;

  static class InnerClass {
    @Icicle String string;
    @Icicle File file;
    @Icicle Parcelable parcelable;
    @Icicle Object object;
  }

}
