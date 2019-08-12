package models;

public interface Serializable {
  String serialize();
  void deserialize(String serialized);
}

