package serialize;

public interface Serializable {
  String serialize();

  void deserialize(String serialized);
}
