package models.cars;

import java.util.Date;

public class Sport extends Car {
  public Sport() {
  }

  public Sport(String brand, String model, String registrationNum, byte[] imageBytes, Date registrationDate,
               String engineCapacity, boolean isAuto, String hourlyCharge) throws Exception {
    super(brand, model, false, registrationNum, imageBytes, registrationDate, engineCapacity, isAuto, hourlyCharge);
    this.type = "Sport";
  }
}
