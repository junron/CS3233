package models;

public interface Serializable {
  byte[] serialize();
  Serializable deserialize(byte[] serialized);
}

