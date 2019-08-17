package models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Pattern;

public class Car implements Serializable {
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

  public Car() {
  }

  public Car(String brand, String model, boolean isEconomy, String registrationNum, byte[] imageBytes,
             Date registrationDate, double engineCapacity, boolean isAuto, double hourlyCharge) throws Exception {
    if (hourlyCharge <= 0) { throw new Exception("Hourly charge must be positive"); }
    Pattern carPlateRegex = Pattern
            .compile("((S[A-Z && [^H]]{1,2})|(E[A-Z]))[0-9]{1,4}[A-Z]", Pattern.CASE_INSENSITIVE);
    if (!carPlateRegex.matcher(registrationNum).matches()) { throw new Exception("Invalid car plate number"); }
    char checkDigit = registrationNum.charAt(registrationNum.length() - 1);
    char correctCheckDigit = Car.getCheckDigit(registrationNum.substring(0, registrationNum.length() - 1));
    if (checkDigit != correctCheckDigit) { throw new Exception("Invalid checksum"); }
    this.brand = brand;
    this.model = model;
    this.isEconomy = isEconomy;
    this.registrationNum = registrationNum;
    this.image = new Image(new ByteArrayInputStream(imageBytes));
    this.imageBytes = imageBytes;
    this.registrationDate = registrationDate;
    this.engineCapacity = engineCapacity;
    this.isAuto = isAuto;
    this.hourlyCharge = hourlyCharge;
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
    return isEconomy ? "Economy" : "Luxury";
  }

  public ImageView getImage() {
    ImageView imageView = new ImageView(image);
    imageView.setFitHeight(100);
    imageView.setFitWidth(100);
    return imageView;
  }

  public String getRegistrationDate() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
    String encoded = Base64.getEncoder().encodeToString(imageBytes);
    return String
            .join("|", new String[]{this.registrationNum, this.brand, this.model, String.valueOf(this.engineCapacity)
                    , String.valueOf(this.isAuto), String.valueOf(this.isEconomy), regTime, encoded});
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
    byte[] bytes = Base64.getDecoder().decode(parts[7]);
    InputStream inputStream = new ByteArrayInputStream(bytes);
    this.image = new Image(inputStream);
  }

  @Override
  public String toString() {
    return "Car{" + "brand='" + brand + '\'' + ", model='" + model + '\'' + ", isEconomy=" + isEconomy + ", " +
            "registrationNum='" + registrationNum + '\'' + ", image=" + image + ", registrationDate=" + registrationDate + ", engineCapacity=" + engineCapacity + ", isAuto=" + isAuto + ", hourlyCharge=" + hourlyCharge + '}';
  }
}