package models;

import models.cars.Car;
import storage.CarStorage;
import storage.UserStorage;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Transaction implements Serializable {
  private Date startTime;
  private Date returnTime;
  private Car car;
  private User user;
  private long serialNumber;

  public Transaction(Date startTime, Date returnTime, Car car, User user) {
    this.startTime = startTime;
    this.returnTime = returnTime;
    this.car = car;
    this.user = user;
    SecureRandom secureRandom = new SecureRandom();
    this.serialNumber = secureRandom.nextLong();
  }

  public Transaction() {
  }

  public Transaction(LocalDateTime startTime, LocalDateTime returnTime, Car car, User user) {
    this(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()), Date
            .from(returnTime.atZone(ZoneId.systemDefault()).toInstant()), car, user);
  }


  @Override
  public String serialize() {
    String startTime = String.valueOf(this.startTime.getTime());
    String returnTime = String.valueOf(this.returnTime.getTime());
    return String
            .join("|", new String[]{String.valueOf(serialNumber), startTime, returnTime, car.getRegistrationNum(),
                    user.getUsername()});
  }

  @Override
  public void deserialize(String serialized) {
    String[] parts = serialized.split("\\|");
    this.serialNumber = Long.parseLong(parts[0]);
    this.startTime = new Date(Long.parseLong(parts[1]));
    this.returnTime = new Date(Long.parseLong(parts[2]));
    this.car = CarStorage.storage.getCarByRegistration(parts[3]);
    this.user = UserStorage.storage.getUserByUsername(parts[4]);
  }
}
