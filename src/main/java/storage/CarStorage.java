package storage;

import models.cars.Car;
import models.Serializable;
import models.cars.Limousine;
import models.cars.SUV;
import models.cars.Sport;

import java.io.IOException;

public class CarStorage extends GeneralStorage {
  public static CarStorage storage;

  public static void initialize() {
    try {
      CarStorage.storage = new CarStorage();
      storage.loadFromFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public CarStorage() throws IOException {
    super("cars.txt");
  }

  public void addCar(Car c) {
    this.objects.add(c);
    this.syncToFile();
  }

  public Car getCarByRegistration(String registrationNum) {
    return (Car) this.objects.stream().filter(car -> {
      if (car instanceof Car) {
        return ((Car) car).getRegistrationNum().equals(registrationNum);
      }
      return false;
    }).findFirst().orElse(null);
  }

  @Override
  protected Serializable deserialize(String serialized) {
    String type = serialized.split("\\|")[9];
    Serializable serializable;
    switch (type) {
      case "Economy": {
        serializable = new Car();
        break;
      }
      case "SUV": {
        serializable = new SUV();
        break;
      }
      case "Sport": {
        serializable = new Sport();
        break;
      }
      default: {
        serializable = new Limousine();
      }
    }
    serializable.deserialize(serialized);
    return serializable;
  }
}
