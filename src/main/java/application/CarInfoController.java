package application;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import models.Transaction;
import models.cars.Car;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CarInfoController implements Initializable {
  static ButtonType confirmButton = new ButtonType("Confirm and pay", ButtonBar.ButtonData.OK_DONE);
  static CarInfoController carInfoController;
  public Text brand;
  public Text type;
  public Text regNo;
  public Text engineCap;
  public Text transmission;
  public ImageView imageView;
  public Text transactionId;
  public Text totalHours;
  public Text totalCost;
  public Text hourlyRate;
  @FXML
  private Text userName, userNameText, transIdText;
  @FXML
  private GridPane bill;
  private Transaction transaction;

  void setTransaction(Transaction transaction, boolean bill) {
    this.transaction = transaction;
    updateUI(bill);
  }


  private void updateUI(boolean bill) {
    Car car = transaction.getCar();
    if (bill) {
      transactionId.setText(String.valueOf(transaction.getSerialNumber()));
      userName.setText(transaction.getUser().getName());
      transIdText.setOpacity(1);
      userNameText.setOpacity(1);
    } else {
      transIdText.setOpacity(0);
      userNameText.setOpacity(0);
    }
    brand.setText(car.getBrandAndModel());
    type.setText(car.getType());
    regNo.setText(car.getRegistrationNum());
    engineCap.setText(car.getEngineCapacity() + "cc");
    imageView.setImage(car.getImage().getImage());
    transmission.setText(car.getTransmission());
    hourlyRate.setText(String.format("$%.2f", car.getHourlyCharge()));
    totalHours.setText(String.valueOf(transaction.getHours()));
    totalCost.setText(String.format("$%.2f", transaction.computeCost()));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    carInfoController = this;
  }

  void triggerBillSave() {
    FileChooser fileChooser = new FileChooser();

    //Set extension filter
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));

    //Prompt user to select a file
    File file = fileChooser.showSaveDialog(null);
    if (file == null) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("File Error");
      alert.setHeaderText("No file selected");
      alert.showAndWait();
      return;
    }

    try {
      WritableImage writableImage = bill.snapshot(new SnapshotParameters(), null);
      RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
      ImageIO.write(renderedImage, "png", file);
    } catch (IOException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("File Error");
      alert.setHeaderText("Error saving bill to file");
      alert.showAndWait();
    }
  }
}
