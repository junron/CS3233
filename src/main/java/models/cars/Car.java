package models.cars;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Serializable;
import utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Pattern;

public abstract class Car implements Serializable {
  private String brand;
  private String model;
  private boolean isEconomy;
  private String registrationNum;
  private Image image;
  private Date registrationDate;
  private double engineCapacity;
  private boolean isAuto;
  private double hourlyCharge;
  private byte[] imageBytes;
  protected String type;

  public Car() {
  }

  public Car(String brand, String model, boolean isEconomy, String registrationNum, byte[] imageBytes,
             Date registrationDate, String engineCapacity, boolean isAuto, String hourlyCharge) throws Exception {
    Pattern carPlateRegex = Pattern
            .compile("((S[A-Z && [^H]]{1,2})|(E[A-Z]))[0-9]{1,4}[A-Z]", Pattern.CASE_INSENSITIVE);
    if (!carPlateRegex.matcher(registrationNum).matches()) { throw new Exception("Invalid car plate number"); }
    char checkDigit = registrationNum.charAt(registrationNum.length() - 1);
    char correctCheckDigit = Car.getCheckDigit(registrationNum.substring(0, registrationNum.length() - 1));
    if (checkDigit != correctCheckDigit) { throw new Exception("Invalid checksum"); }
    if (brand == null || brand.length() == 0) throw new Exception("Brand cannot be empty");
    if (model == null || model.length() == 0) throw new Exception("Model cannot be empty");
    if (imageBytes == null || imageBytes.length == 0) throw new Exception("Image cannot be empty");
    if (registrationDate == null) throw new Exception("Invalid registration date");
    if (!Utils.isPast(registrationDate)) throw new Exception("Registration date cannot be in future");
    try {
      this.engineCapacity = Double.parseDouble(engineCapacity);
      if (this.engineCapacity < 0) throw new Exception("Invalid engine capacity");
    } catch (NumberFormatException e) {
      throw new Exception("Invalid engine capacity");
    }
    try {
      this.hourlyCharge = Double.parseDouble(hourlyCharge);
      if (this.hourlyCharge < 0) throw new Exception("Invalid hourly rate");
    } catch (NumberFormatException e) {
      throw new Exception("Invalid hourly rate");
    }
    this.brand = brand;
    this.model = model;
    this.isEconomy = isEconomy;
    this.registrationNum = registrationNum;
    this.image = new Image(new ByteArrayInputStream(imageBytes));
    this.imageBytes = imageBytes.clone();
    this.registrationDate = registrationDate;
    this.isAuto = isAuto;
  }

  private static char getCheckDigit(String registrationNumber) {
    char[] registrationNum = registrationNumber.toUpperCase().toCharArray();
    char[] checkDigits = "AZYXUTSRPMLKJHGEDCB".toCharArray();
    int[] multiply = new int[]{9, 4, 5, 4, 3, 2};
    int sum = 0;
    int startIndex = 0;
    if (!Character.isDigit(registrationNum[2])) {
      //      3 letter prefix
      //      Ignore first character (S)
      startIndex = 1;
    }
    //    Offset for padding
    int numDigits = registrationNum.length - (startIndex + 2);
    for (int i = startIndex; i < registrationNum.length; i++) {
      if (i - startIndex < 2) {
        //        Letters
        sum += ((int) registrationNum[i] - (int) ('A') + 1) * multiply[i - startIndex];
      } else {
        //        Digits
        sum += Integer.parseInt("" + registrationNum[i]) * multiply[i - startIndex + (4 - numDigits)];
      }
    }
    return checkDigits[sum % 19];
  }

  public double getHourlyCharge() {
    return hourlyCharge;
  }

  public void setHourlyCharge(double hourlyCharge) {
    this.hourlyCharge = hourlyCharge;
  }

  public String getBrandAndModel() {
    return brand + " - " + model;
  }

  public String getType() {
    return isEconomy ? "Economy" : "Luxury - " + this.type;
  }

  public byte[] getImageBytes() {
    return imageBytes;
  }

  public ImageView getImage() {
    ImageView imageView = new ImageView(image);
    imageView.setPreserveRatio(true);
    imageView.setFitWidth(120);
    return imageView;
  }

  public String getRegistrationDate() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    return simpleDateFormat.format(registrationDate);
  }

  public double getEngineCapacity() {
    return engineCapacity;
  }

  public String getTransmission() {
    return isAuto ? "Auto" : "Manual";
  }

  public String getRegistrationNum() {
    return registrationNum;
  }

  @Override
  public String serialize() {
    String regTime = String.valueOf(this.registrationDate.getTime());
    String encoded = Base64.getEncoder().encodeToString(this.imageBytes);
    return String
            .join("|", new String[]{this.registrationNum, this.brand, this.model, String.valueOf(this.engineCapacity)
                    , String.valueOf(this.isAuto), String.valueOf(this.isEconomy), regTime,
                    String.valueOf(this.hourlyCharge), encoded, this.type});
  }


  @Override
  public void deserialize(String serialized) {
    String[] parts = serialized.split("\\|");
    this.registrationNum = parts[0];
    this.brand = parts[1];
    this.model = parts[2];
    this.engineCapacity = Double.parseDouble(parts[3]);
    this.isAuto = Boolean.parseBoolean(parts[4]);
    this.isEconomy = Boolean.parseBoolean(parts[5]);
    this.registrationDate = new Date(Long.parseLong(parts[6]));
    this.hourlyCharge = Double.parseDouble(parts[7]);
    this.imageBytes = Base64.getDecoder().decode(parts[8]);
    InputStream inputStream = new ByteArrayInputStream(this.imageBytes);
    this.image = new Image(inputStream);
    this.type = parts[9];
  }

  @Override
  public String toString() {
    return "Car{" + "brand='" + brand + '\'' + ", model='" + model + '\'' + ", isEconomy=" + isEconomy + ", " +
            "registrationNum='" + registrationNum + '\'' + ", image=" + image + ", registrationDate=" + registrationDate + ", engineCapacity=" + engineCapacity + ", isAuto=" + isAuto + ", hourlyCharge=" + hourlyCharge + ", imageBytes=" + Arrays
            .toString(imageBytes) + ", type='" + type + '\'' + '}';
  }
}