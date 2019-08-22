package storage;

import models.Serializable;
import models.cars.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

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

  public void addCar(models.cars.Car c) {
    this.objects.add(c);
    this.syncToFile();
  }

  public models.cars.Car getCarByRegistration(String registrationNum) {
    return (models.cars.Car) this.objects.stream().filter(car -> {
      if (car instanceof models.cars.Car) {
        return ((models.cars.Car) car).getRegistrationNum().equals(registrationNum);
      }
      return false;
    }).findFirst().orElse(null);
  }


  public ArrayList<Car> filter(Function<Car, Boolean> predicate) {
    ArrayList<Car> result = new ArrayList<>();
    for (Serializable car : getObjects()) {
      if (car instanceof Car) {
        if (predicate.apply((Car) car)) {
          result.add((Car) car);
        }
      }
    }
    return result;
  }

  public ArrayList<Object> map(Function<Car, Object> mapping) {
    ArrayList<Object> result = new ArrayList<>();
    for (Serializable car : getObjects()) {
      if (car instanceof Car) result.add(mapping.apply((Car) car));
    }
    return result;
  }

  public ArrayList<Object> mapUnique(Function<Car, Object> mapping) {
    ArrayList<Object> result = new ArrayList<>();
    for (Serializable car : getObjects()) {
      if (car instanceof Car) {
        Object res = mapping.apply((Car) car);
        if (!result.contains(res)) result.add(res);
      }
    }
    return result;
  }

  @Override
  protected Serializable deserialize(String serialized) {
    String type = serialized.split("\\|")[9];
    Serializable car;
    switch (type) {
      case "Economy": {
        car = new Economy();
        break;
      }
      case "SUV": {
        car = new SUV();
        break;
      }
      case "Sport": {
        car = new Sport();
        break;
      }
      default: {
        car = new Limousine();
      }
    }
    car.deserialize(serialized);
    return car;
  }
}
