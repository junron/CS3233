package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Serializable;
import storage.CarStorage;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public class AdminController implements Initializable {
  public static AdminController adminController;
  @FXML
  private Button resetStatus;
  @FXML
  private TableView<models.cars.Car> carTable;

  private Map<String, String> keyPropertyMappings = ofEntries(entry("Registration No", "registrationNum"), entry(
          "Model", "brandAndModel"), entry("Type", "type"), entry("Image", "image"), entry("Engine Capacity",
          "engineCapacity"), entry("Registration Date", "registrationDate"), entry("Transmission", "transmission"),
          entry("Hourly rate", "hourlyCharge"), entry("Status", "status"));

  public void triggerOpenAddCar() {
    ScreenController.activate("addcar");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    AdminController.adminController = this;

    for (TableColumn column : carTable.getColumns()) {
      column.setCellValueFactory(new PropertyValueFactory<>(keyPropertyMappings.get(column.getText())));
    }
    for (Serializable s : CarStorage.storage.getObjects()) {
      if (s instanceof models.cars.Car) {
        carTable.getItems().add((models.cars.Car) s);
      }
    }

    carTable.getSelectionModel().selectedItemProperty().addListener(((_observable, _oldValue, newValue) -> {
      if (newValue.getStatus().getText().equals("Available")) {
        resetStatus.setDisable(true);
      } else {
        resetStatus.setDisable(false);
      }
    }));
  }

  void rerender() {
    carTable.getItems().clear();
    for (Serializable s : CarStorage.storage.getObjects()) {
      if (s instanceof models.cars.Car) {
        carTable.getItems().add((models.cars.Car) s);
      }
    }
  }

  @FXML
  private void triggerSignout() {
    ScreenController.activate("main");
  }

  @FXML
  private void triggerResetStatus() {
//    int serialNo = TransactionStoragecarTable.getSelectionModel().getSelectedItem()
  }
}
