package serialize;

public interface Serializable {
  byte[] serialize();
  void deserialize(byte[] serialized);
}
