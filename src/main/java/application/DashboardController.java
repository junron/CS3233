package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.text.Text;
import models.User;
import utils.Utils;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import static javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

public class DashboardController implements Initializable {
  static DashboardController dashboardController;
  @FXML
  private Button selectBtn;
  @FXML
  private DatePicker startDate, returnDate;
  @FXML
  private Spinner<Integer> startHour;
  @FXML
  private Spinner<Integer> returnHour;
  @FXML
  private Text welcome, output;
  private User user;

  void setUser(User user) {
    this.user = user;
    GalleryController.galleryController.setUser(user);
    if (dashboardController == null) return;
    welcome.setText("Welcome, " + user.getName());
  }

  @FXML
  private void triggerSignout() {
    user = null;
    this.returnDate.setValue(null);
    this.returnHour.getValueFactory().setValue(0);
    ScreenController.activate("main");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    dashboardController = this;
    if (this.user != null) welcome.setText("Welcome, " + user.getName());

    startHour.setValueFactory(new IntegerSpinnerValueFactory(0, 23));
    //    Set start time to now + 1h
    startHour.getValueFactory().setValue(LocalDateTime.now().getHour() + 1);
    startHour.getValueFactory().valueProperty().addListener((a, b, c) -> rerenderStuff());
    returnHour.setValueFactory(new IntegerSpinnerValueFactory(0, 23));
    returnHour.getValueFactory().valueProperty().addListener((a, b, c) -> rerenderStuff());

    this.startDate.setValue(LocalDate.now());
    startDate.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (returnDate.getValue() != null) {
          //          Start date must be <= returnDate
          if (item.compareTo(returnDate.getValue()) > 0) {
            setDisable(true);
            return;
          }
        }
        setDisable(empty || Utils.isPast(item));
      }
    });
    returnDate.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        setDisable(empty || item.compareTo(startDate.getValue()) < 0);
      }
    });
  }

  @FXML
  private void rerenderStuff() {
    LocalDateTime startTime = this.startDate.getValue().atStartOfDay().plusHours(startHour.getValue());
    if (startTime.compareTo(LocalDateTime.now()) < 0) {
      output.setText("Start time must not be in past");
      selectBtn.setDisable(true);
      return;
    }
    if (this.returnDate.getValue() == null) {
      output.setText("Select return time");
      selectBtn.setDisable(true);
      return;
    }
    LocalDateTime returnTime = this.returnDate.getValue().atStartOfDay().plusHours(returnHour.getValue());
    if (returnTime.compareTo(LocalDateTime.now()) < 0) {
      output.setText("Return time must not be in past");
      selectBtn.setDisable(true);
      return;
    }
    int hours = (int) startTime.until(returnTime, ChronoUnit.HOURS);
    if (hours < 1) {
      output.setText("Return time must be after start time");
      selectBtn.setDisable(true);
      return;
    }
    output.setText("");
    selectBtn.setDisable(false);
  }

  @FXML
  private void triggerCarSelect() {
    LocalDateTime startTime = this.startDate.getValue().atStartOfDay().plusHours(startHour.getValue());
    LocalDateTime returnTime = this.returnDate.getValue().atStartOfDay().plusHours(returnHour.getValue());
    GalleryController.galleryController.setStartTime(startTime);
    GalleryController.galleryController.setReturnTime(returnTime);
    ScreenController.activate("gallery");
  }
}
