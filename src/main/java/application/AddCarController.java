package application;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import models.Car;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import static storage.CarStorage.storage;

public class AddCarController implements Initializable {
  public TextField engineCap;
  public ComboBox<String> type;
  public TextField regNo;
  public ComboBox<String> transm;
  public ComboBox<String> category;
  public DatePicker regDate;
  public TextField brand;
  public TextField model;
  public Text output;
  public Label selectedFile;

  private byte[] imageBytes;

  public void triggerBack() {
    ScreenController.activate("admin");
  }

  public void chooseImage() throws IOException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select image");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg"));
    File file = fileChooser.showOpenDialog(null);
    imageBytes = Files.readAllBytes(Path.of(file.toURI()));
    selectedFile.setText("Selected file: " + file.getName());
  }

  public void triggerAddCar() {
    try {
      Date date;
      try {
        date = Date.from(Instant.from(regDate.getValue().atStartOfDay(ZoneId.systemDefault())));
      } catch (Exception e) {
        throw new Exception("Invalid date");
      }
      boolean isAuto = transm.getSelectionModel().getSelectedItem().equals("Auto");
      Car car = new Car(brand.getText(), model.getText(), true, regNo.getText(), imageBytes, date, Double
              .parseDouble(engineCap.getText()),isAuto,1);
      if (storage.getCarByRegistration(regNo.getText()) != null) {
        output.setText("Car with same car plate number exists.");
        return;
      }
      storage.addCar(car);
      this.triggerBack();
      AdminController.adminController.rerender();
    } catch (Exception e) {
      output.setText(e.getMessage());
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    transm.setItems(FXCollections.observableArrayList("Auto", "Manual"));
    category.setItems(FXCollections.observableArrayList("Economy", "Luxury"));
    category.setOnAction(event -> {
      if (category.getSelectionModel().getSelectedItem().equals("Economy")) {
        type.getItems().add("Economy");
        type.getSelectionModel().select("Economy");
        type.setDisable(true);
      } else {
        type.getItems().remove("Economy");
        type.getSelectionModel().select("SUV");
        type.setDisable(false);
      }
    });
    type.setItems(FXCollections.observableArrayList("SUV", "Limousine", "Sport"));

    regDate.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        LocalDate today = LocalDate.now();
        setDisable(empty || item.compareTo(today) > 0);
      }
    });
  }
}
