package models;

import models.cars.Car;
import storage.CarStorage;
import storage.UserStorage;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class Transaction implements Serializable {
  private LocalDateTime startTime;
  private LocalDateTime returnTime;
  private Car car;
  private User user;
  private int serialNumber;
  private long hours;

  public Transaction(LocalDateTime startTime, LocalDateTime returnTime, Car car, User user) {
    this.startTime = startTime;
    this.returnTime = returnTime;
    this.car = car;
    this.user = user;
    SecureRandom secureRandom = new SecureRandom();
    this.serialNumber = Math.abs(secureRandom.nextInt());
    this.hours = startTime.until(returnTime, ChronoUnit.HOURS);
  }

  public Transaction() {
  }

  public Car getCar() {
    return car;
  }

  public int getSerialNumber() {
    return serialNumber;
  }

  public User getUser() {
    return user;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getReturnTime() {
    return returnTime;
  }

  public long getHours() {
    return hours;
  }

  public double computeCost() {
    return hours * car.getHourlyCharge();
  }


  @Override
  public String serialize() {
    String startTime = String.valueOf(this.startTime.toInstant(ZoneOffset.UTC).getEpochSecond());
    String returnTime = String.valueOf(this.returnTime.toInstant(ZoneOffset.UTC).getEpochSecond());
    return String
            .join("|", new String[]{String.valueOf(serialNumber), startTime, returnTime, car.getRegistrationNum(),
                    user.getUsername()});
  }

  @Override
  public void deserialize(String serialized) {
    String[] parts = serialized.split("\\|");
    this.serialNumber = Integer.parseInt(parts[0]);
    this.startTime = LocalDateTime.ofEpochSecond(Long.parseLong(parts[1]), 0, ZoneOffset.UTC);
    this.returnTime = LocalDateTime.ofEpochSecond(Long.parseLong(parts[2]), 0, ZoneOffset.UTC);
    this.car = CarStorage.storage.getCarByRegistration(parts[3]);
    this.user = UserStorage.storage.getUserByUsername(parts[4]);
    this.hours = startTime.until(returnTime, ChronoUnit.HOURS);
  }
}
