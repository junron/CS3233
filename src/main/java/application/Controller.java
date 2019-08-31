package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class Controller {
  @FXML
  private void clicked() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setHeaderText("Yay");
    alert.setTitle("Drop bio");
    alert.showAndWait();
  }
}
