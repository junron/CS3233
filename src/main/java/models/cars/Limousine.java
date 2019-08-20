package models.cars;

import java.util.Date;

public class Limousine extends Car {
  public Limousine() {
  }

  public Limousine(String brand, String model, String registrationNum, byte[] imageBytes, Date registrationDate,
                   String engineCapacity, boolean isAuto, String hourlyCharge) throws Exception {
    super(brand, model, false, registrationNum, imageBytes, registrationDate, engineCapacity, isAuto, hourlyCharge);
    this.type = "Limousine";
  }
}
