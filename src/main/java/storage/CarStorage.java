package storage;

import models.Car;
import models.Serializable;

import java.io.IOException;

public class CarStorage extends GeneralStorage {
  public static CarStorage storage;

  public static void initialize(){
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

  public void addCar(Car c){
    this.objects.add(c);
    this.syncToFile();
  }

  public Car getCarByRegistration(String registrationNum){
    return (Car) this.objects.stream().filter(car ->{
      if(car instanceof Car){
        return ((Car) car).getRegistrationNum().equals(registrationNum);
      }
      return false;
    }).findFirst().orElse(null);
  }

  @Override
  protected Serializable deserialize(String serialized) {
    Serializable serializable = new Car();
    serializable.deserialize(serialized);
    return serializable;
  }
}
