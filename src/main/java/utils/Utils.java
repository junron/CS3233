package utils;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Utils {
  public static boolean isPast(Date date) {
    return date.getTime() < new Date().getTime();
  }

  public static boolean isPast(LocalDate date) {
    LocalDate today = LocalDate.now();
    return date.compareTo(today) < 0;
  }

  public static boolean isPast(LocalDateTime date) {
    LocalDateTime now = LocalDateTime.now();
    return date.compareTo(now) < 0;
  }

  public static HBox showSearch(String text, String search) {
    search = search.toLowerCase();
    HBox hBox = new HBox();
    int prev = 0;
    int i = text.toLowerCase().indexOf(search);
    if (i == -1 || search.length() == 0) {
      hBox.getChildren().add(new Text(text));
      return hBox;
    }
    while (true) {
      //      Handle prev
      hBox.getChildren().add(new Text(text.substring(prev, i)));
      //      Highlight search result
      Label label = new Label(text.substring(i, i + search.length()));
      label.setStyle("-fx-background-color: yellow");
      hBox.getChildren().add(label);
      int index = text.toLowerCase().indexOf(search, i + 1);
      if(index==-1) break;
      prev = i+1;
      i = index;
    }
    hBox.getChildren().add(new Text(text.substring(i+search.length())));
    return hBox;
  }

}
