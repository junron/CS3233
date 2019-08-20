package storage;

import models.cars.*;
import models.Serializable;

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

  public ArrayList<Car> filter(Function<Car,Boolean> predicate){
    ArrayList<Car> result = new ArrayList<>();
    for(Serializable s: getObjects()){
      if(s instanceof Car){
        if(predicate.apply((Car) s)){
          result.add((Car) s);
        }
      }
    }
    return result;
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
        serializable = new Economy();
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
