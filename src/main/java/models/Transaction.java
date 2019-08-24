package models;

import models.cars.Car;
import storage.CarStorage;
import storage.UserStorage;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Transaction implements Serializable {
  private Date startTime;
  private Date returnTime;
  private Car car;
  private User user;
  private int serialNumber;
  private long hours;

  public Transaction(Date startTime, Date returnTime, Car car, User user) {
    this.startTime = startTime;
    this.returnTime = returnTime;
    this.car = car;
    this.user = user;
    SecureRandom secureRandom = new SecureRandom();
    this.serialNumber = Math.abs(secureRandom.nextInt());
  }

  public Transaction() {
  }

  public Transaction(LocalDateTime startTime, LocalDateTime returnTime, Car car, User user) {
    this(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()), Date
            .from(returnTime.atZone(ZoneId.systemDefault()).toInstant()), car, user);
    this.hours = startTime.until(returnTime, ChronoUnit.HOURS);
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

  public Date getStartTime() {
    return startTime;
  }

  public Date getReturnTime() {
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
    String startTime = String.valueOf(this.startTime.getTime());
    String returnTime = String.valueOf(this.returnTime.getTime());
    return String
            .join("|", new String[]{String.valueOf(serialNumber), startTime, returnTime, car.getRegistrationNum(),
                    user.getUsername()});
  }

  @Override
  public void deserialize(String serialized) {
    String[] parts = serialized.split("\\|");
    this.serialNumber = Integer.parseInt(parts[0]);
    this.startTime = new Date(Long.parseLong(parts[1]));
    this.returnTime = new Date(Long.parseLong(parts[2]));
    this.car = CarStorage.storage.getCarByRegistration(parts[3]);
    this.user = UserStorage.storage.getUserByUsername(parts[4]);
  }
}
