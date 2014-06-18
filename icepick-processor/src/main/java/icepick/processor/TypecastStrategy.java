package icepick.processor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import javax.lang.model.type.TypeMirror;

public class TypecastStrategy {

  public static String resolve(String bundleMethod, TypeMirror fieldType) {
    if (TYPE_ERASURE.containsKey(bundleMethod)) {
      return "(" + fieldType + ") (" + TYPE_ERASURE.get(bundleMethod) + ")";
    }

    if (SIMPLE.contains(bundleMethod)) {
      return "(" + fieldType + ")";
    }

    return "";
  }

  private static final Map<String, String> TYPE_ERASURE = ImmutableMap.of(
      "ParcelableArrayList", "java.util.ArrayList",
      "SparseParcelableArray", "android.util.SparseArray");

  private static final Set<String> SIMPLE = ImmutableSet.of(
      "IntegerArrayList",
      "StringArrayList",
      "CharSequenceArrayList",
      "CharSequence",
      "CharSequenceArray",
      "Parcelable",
      "ParcelableArray",
      "Serializable");
}
