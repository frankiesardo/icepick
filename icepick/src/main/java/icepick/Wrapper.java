package icepick;

import android.os.Parcel;
import android.os.Parcelable;

class Wrapper implements Parcelable {
  public static final Parcelable.Creator<Wrapper> CREATOR = new Parcelable.Creator<Wrapper>() {
    @Override
    public Wrapper createFromParcel(Parcel in) {
      String json = in.readString();
      return new Wrapper(json);
    }

    @Override
    public Wrapper[] newArray(int size) {
      return new Wrapper[size];
    }
  };
  final String json;

  Wrapper(String json) {
    this.json = json;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeString(json);
  }
}