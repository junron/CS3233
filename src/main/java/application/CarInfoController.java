package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import models.Transaction;
import models.User;
import models.cars.Car;
import utils.Utils;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import static javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

public class CarInfoController implements Initializable {
  public static ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
  public Text brand;
  public Text type;
  public Text regNo;
  public Text engineCap;
  public Text transmission;
  public ImageView imageView;
  private static User user;
  private static Car car;
  private static CarInfoController carInfoController;
  private static Transaction transaction;
  public DatePicker startDate;
  public Spinner<Integer> startHour;
  public Text totalHours;
  public Text totalCost;
  public Spinner<Integer> returnHour;
  public DatePicker returnDate;
  public Text hourlyRate;
  private int hours;
  private static Dialog<ButtonType> dialog;

  public static void setCar(Car car) {
    CarInfoController.car = car;
    carInfoController.updateUI();
  }

  public static void setUser(User user) {
    CarInfoController.user = user;
    carInfoController.updateUI();
  }

  public static void setDialog(Dialog<ButtonType> dialog) {
    CarInfoController.dialog = dialog;
    carInfoController.updateUI();
  }

  public static Transaction getTransaction() {
    Transaction transaction = CarInfoController.transaction;
    CarInfoController.transaction = null;
    return transaction;
  }

  private void updateUI() {
    brand.setText(car.getBrandAndModel());
    type.setText(car.getType());
    regNo.setText(car.getRegistrationNum());
    engineCap.setText(car.getEngineCapacity() + "cc");
    imageView.setImage(car.getImage().getImage());
    transmission.setText(car.getTransmission());
    hourlyRate.setText("$" + car.getHourlyCharge());
    updateHours();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    carInfoController = this;
    startHour.setValueFactory(new IntegerSpinnerValueFactory(0, 23));
    returnHour.setValueFactory(new IntegerSpinnerValueFactory(0, 23));
    //    Update stuff when input changes
    startHour.valueProperty().addListener(e -> updateHours());
    returnHour.valueProperty().addListener(e -> updateHours());

    startDate.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (returnDate.getValue() != null) {
          //          Start date must be <= returnDate
          if (item.compareTo(returnDate.getValue()) > 0) {
            setDisable(true);
          }
        }
        setDisable(empty || Utils.isPast(item));
      }
    });
    startDate.setValue(LocalDate.now());
    returnDate.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        setDisable(empty || item.compareTo(startDate.getValue()) < 0);
      }
    });
    startHour.getValueFactory().setValue(LocalDateTime.now().getHour());


  }

  @FXML
  private void updateHours() {
    int thisHours = LocalDateTime.now().getHour();
    if (Utils.isPast(this.startDate.getValue().atStartOfDay())) {
      //      user is renting car today
      //      Prevent user from setting past hour
      ((IntegerSpinnerValueFactory) startHour.getValueFactory()).setMin(thisHours);
    } else {
      ((IntegerSpinnerValueFactory) startHour.getValueFactory()).setMin(0);
    }
    //    Null check on return date
    if (this.returnDate.getValue() == null || returnHour.getValue() == null) {
      if (dialog == null) return;
      dialog.getDialogPane().lookupButton(confirmButton).setDisable(true);
      return;
    }
    if (Utils.isPast(this.returnDate.getValue().atStartOfDay())) {
      //      user is returning car today
      //      Prevent user from setting past hour
      ((IntegerSpinnerValueFactory) returnHour.getValueFactory()).setMin(thisHours + 1);
    } else {
      ((IntegerSpinnerValueFactory) returnHour.getValueFactory()).setMin(0);
    }
    LocalDateTime startTime = this.startDate.getValue().atStartOfDay().plusHours(startHour.getValue());
    LocalDateTime returnTime = this.returnDate.getValue().atStartOfDay().plusHours(returnHour.getValue());
    this.hours = (int) startTime.until(returnTime, ChronoUnit.HOURS);
    //    Prevent non positive cost
    if (hours < 1) {
      LocalDateTime newReturnTime = startTime.plusHours(1);
      this.returnDate.setValue(LocalDate.from(newReturnTime.toLocalDate().atStartOfDay()));
      this.returnHour.getValueFactory().setValue(newReturnTime.getHour());
    }
    this.totalHours.setText(String.valueOf(hours));
    if (car == null) return;
    this.totalCost.setText("$" + car.getHourlyCharge() * hours);
    //    Store transaction
    transaction = new Transaction(startTime, returnTime, car, user);
    //    Reenable confirm button if validations pass
    dialog.getDialogPane().lookupButton(confirmButton).setDisable(false);
  }
}
