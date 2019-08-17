package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Car;
import models.Serializable;
import storage.CarStorage;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public class AdminController implements Initializable {
  @FXML
  private TableView<Car> carTable;

  private Map<String,String> keyPropertyMappings = ofEntries(
          entry("Reg No","registrationNum"),
          entry("Model","brandAndModel"),
          entry("Type","type"),
          entry("Image","image"),
          entry("Engine cap","engineCapacity"),
          entry("Reg Date","registrationDate"),
          entry("Transmission","transmission")
  );
  public void triggerOpenAddCar() {
    ScreenController.activate("addcar");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    for(TableColumn column: carTable.getColumns()){
      column.setCellValueFactory(new PropertyValueFactory<>(keyPropertyMappings.get(column.getText())));
    }
    for(Serializable s: CarStorage.storage.getObjects()){
      if(s instanceof Car){
        carTable.getItems().add((Car) s);
      }
    }
  }
}
