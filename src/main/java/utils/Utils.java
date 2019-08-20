package utils;

import java.time.LocalDate;
import java.util.Date;

public class Utils {
  public static boolean isPast(Date date){
    return date.getTime()<new Date().getTime();
  }
  public static boolean isPast(LocalDate date){
    LocalDate today = LocalDate.now();
    return date.compareTo(today)<0;
  }

}
