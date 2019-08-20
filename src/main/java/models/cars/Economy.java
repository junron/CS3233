package models.cars;

import java.util.Date;

public class Economy extends Car {
  public Economy() {
  }

  public Economy(String brand, String model, String registrationNum, byte[] imageBytes, Date registrationDate,
                 String engineCapacity, boolean isAuto, String hourlyCharge) throws Exception {
    super(brand, model, true, registrationNum, imageBytes, registrationDate, engineCapacity, isAuto, hourlyCharge);
    this.type = "Economy";
  }
}
