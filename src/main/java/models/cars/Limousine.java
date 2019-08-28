package models.cars;

import java.util.Date;

public class Limousine extends Car {
  private boolean hasChauffeur;
  public Limousine() {
  }

  public Limousine(String brand, String model, String registrationNum, byte[] imageBytes, Date registrationDate, String engineCapacity, boolean isAuto, String hourlyCharge, boolean hasChauffeur) throws Exception {
    super(brand, model, false, registrationNum, imageBytes, registrationDate, engineCapacity, isAuto, hourlyCharge);
    this.hasChauffeur = hasChauffeur;
    this.type = "Limousine";
  }

  public boolean isHasChauffeur() {
    return hasChauffeur;
  }

  @Override
  public String serialize() {
    return super.serialize()+"|"+hasChauffeur;
  }

  @Override
  public void deserialize(String serialized) {
    super.deserialize(serialized);
    this.hasChauffeur = Boolean.parseBoolean(serialized.split("\\|")[10]);
  }
}
