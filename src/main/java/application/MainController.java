package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import models.User;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static storage.UserStorage.storage;

public class MainController implements Initializable {

  @FXML
  private Button signinBtn;

  @FXML
  private TextField signinUsername;

  @FXML
  private PasswordField signinPassword;

  @FXML
  private Text signinOutput;

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
      User user = new User(signupUsername.getText(), signupPassword.getText(), signupName.getText(),
              signupNRIC.getText(), signupDOB.getValue());
      if (storage.getUserByUsername(signupUsername.getText()) != null) {
        signupOutput.setText("Username already taken.");
        return;
      }
      storage.addUser(user);
      signupOutput.setFill(Color.GREEN);
      signupOutput.setText("Signed up successfully.");
    } catch (Exception e) {
      signupOutput.setText(e.getMessage());
    }

  }

  public void triggerSignIn() {
    User user = storage.getUserByUsername(signinUsername.getText());
    if (user == null) {
      signinOutput.setText("User not found");
      return;
    }
    if (user.signIn(signinPassword.getText())) {
      signinOutput.setText("Signed in successfully");
      signinOutput.setFill(Color.GREEN);
    } else {
      signinOutput.setText("Incorrect password");
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    signupDOB.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        LocalDate today = LocalDate.now();
        setDisable(empty || item.compareTo(today) > 0);
      }
    });
  }
}

