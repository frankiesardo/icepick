package icepick;

public class UnableToSerializeException extends RuntimeException {
  public UnableToSerializeException(Throwable throwable) {
    super(throwable);
  }
}
