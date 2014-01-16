package icepick;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

class GsonParcer {

  private final static Gson GSON = new Gson();

  static <T> String encode(T instance) throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonWriter writer = new JsonWriter(stringWriter);

    try {
      Class<?> type = instance.getClass();

      writer.beginObject();
      writer.name(type.getName());
      GSON.toJson(instance, type, writer);
      writer.endObject();

      return stringWriter.toString();
    } finally {
      writer.close();
    }
  }

  static <T> T decode(String json) throws IOException {
    JsonReader reader = new JsonReader(new StringReader(json));

    try {
      reader.beginObject();

      Class<?> type = Class.forName(reader.nextName());
      return GSON.fromJson(reader, type);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } finally {
      reader.close();
    }
  }
}
