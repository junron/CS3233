package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import models.User;
import utils.Utils;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static storage.UserStorage.storage;

public class SignupController implements Initializable {
  @FXML
  private TextField signupUsername;

  @FXML
  private PasswordField signupPassword;

  @FXML
  private Text signupOutput;

  @FXML
  private TextField signupName;

  @FXML
  private TextField signupNRIC;

  @FXML
  private DatePicker signupDOB;

  public void triggerSignUp() {
    try {
      User user = new User(signupUsername.getText(), signupPassword.getText(), signupName.getText(), signupNRIC
              .getText(), signupDOB.getValue());
      if (storage.getUserByUsername(signupUsername.getText()) != null) {
        signupOutput.setText("Username already taken.");
        return;
      }
      storage.addUser(user);
      signupDOB.getEditor().setText("");
      signupName.setText("");
      signupPassword.setText("");
      signupNRIC.setText("");
      signupUsername.setText("");
      GalleryController.setUser(user);
      ScreenController.activate("gallery");
    } catch (Exception e) {
      signupOutput.setText(e.getMessage());
    }

  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    signupDOB.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        setDisable(empty || !Utils.isPast(item));
      }
    });
  }

  public void triggerBack() {
    ScreenController.activate("main");
  }
}
