package application;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import models.cars.*;
import utils.Utils;

import java.io.ByteArrayInputStream;
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
  public TextField hourlyRate;
  public ImageView imageView;
  public CheckBox includesChauffeur;

  private byte[] imageBytes;

  public void triggerBack() {
    ScreenController.activate("admin");
  }

  public void chooseImage() throws IOException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select image");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg"));
    File file = fileChooser.showOpenDialog(null);
    this.imageBytes = Files.readAllBytes(Path.of(file.toURI()));
    Image image = new Image(new ByteArrayInputStream(imageBytes));
    imageView.setImage(image);
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
      Car car;
      switch (type.getSelectionModel().getSelectedItem()) {
        case "Economy": {
          car = new Economy(brand.getText(), model.getText(), regNo.getText(), imageBytes, date, engineCap
                  .getText(), isAuto, hourlyRate.getText());
          break;
        }
        case "SUV": {
          car = new SUV(brand.getText(), model.getText(), regNo.getText(), imageBytes, date, engineCap
                  .getText(), isAuto, hourlyRate.getText());
          break;
        }
        case "Limousine": {
          car = new Limousine(brand.getText(), model.getText(), regNo.getText(), imageBytes, date, engineCap
                  .getText(), isAuto, hourlyRate.getText(), includesChauffeur.isSelected());
          break;
        }
        case "Sport": {
          car = new Sport(brand.getText(), model.getText(), regNo.getText(), imageBytes, date, engineCap
                  .getText(), isAuto, hourlyRate.getText());
          break;
        }
        default: {
          throw new Exception("A bug has occurred");
        }
      }
      if (storage.getCarByRegistration(regNo.getText()) != null) {
        output.setText("Car with same car plate number exists.");
        return;
      }
      storage.addCar(car);
      //      Reset fields
      brand.setText("");
      model.setText("");
      regNo.setText("");
      engineCap.setText("");
      hourlyRate.setText("");
      imageView.setImage(null);
      imageBytes = null;
      regDate.getEditor().setText("");
      transm.getSelectionModel().select("Auto");
      category.getSelectionModel().select("Economy");
      type.getItems().add("Economy");
      type.getSelectionModel().select("Economy");
      includesChauffeur.setDisable(true);
      type.setDisable(true);

      this.triggerBack();
      AdminController.adminController.rerender();
    } catch (Exception e) {
      output.setText(e.getMessage());
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    transm.setItems(FXCollections.observableArrayList("Auto", "Manual"));
    transm.getSelectionModel().select("Auto");
    category.setItems(FXCollections.observableArrayList("Economy", "Luxury"));
    category.getSelectionModel().select("Economy");
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
    type.setOnAction(event -> {
      if (type.getSelectionModel().getSelectedItem().equals("Limousine")) {
        includesChauffeur.setDisable(false);
      } else {
        includesChauffeur.setDisable(true);
      }
    });
    type.setItems(FXCollections.observableArrayList("SUV", "Limousine", "Sport"));
    type.getItems().add("Economy");
    type.getSelectionModel().select("Economy");
    type.setDisable(true);
    regDate.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        setDisable(empty || !Utils.isPast(item));
      }
    });
  }
}
