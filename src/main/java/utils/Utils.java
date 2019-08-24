package utils;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Utils {
  public static boolean isPast(Date date){
    return date.getTime()<new Date().getTime();
  }
  public static boolean isPast(LocalDate date){
    LocalDate today = LocalDate.now();
    return date.compareTo(today)<0;
  }
  public static boolean isPast(LocalDateTime date){
    LocalDateTime now = LocalDateTime.now();
    return date.compareTo(now)<0;
  }

}
