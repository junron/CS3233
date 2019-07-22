package application;

import javafx.scene.control.Alert;

public class FxAlerts {
  public static Alert showErrorDialog(String title, String header, String message) {
    Alert currentAlert = new Alert(Alert.AlertType.ERROR);
    currentAlert.setTitle(title);
    currentAlert.setHeaderText(header);
    currentAlert.setContentText(message);
    return currentAlert;
  }

  public static Alert showErrorDialog(String title, String message) {
    return showErrorDialog(title, null, message);
  }

}
