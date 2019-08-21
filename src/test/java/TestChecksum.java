import models.cars.Car;

public class TestChecksum {
  public static void main(String[] args) {
    String prefix = "SHI";
    for(int i=0;i<10000;i++){
      char checkDigit = Car.getCheckDigit(prefix+""+i);
      if (checkDigit == 'T') {
        System.out.println(prefix+""+i+checkDigit);
      }
    }
  }
}
