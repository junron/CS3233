package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import models.User;

public class MainController {

  @FXML
  private Button signinBtn;

  @FXML
  private TextField signinUsername;

  @FXML
  private PasswordField signinPassword;

  @FXML
  private Text signinOutput;

  @FXML
  private Button signupBtn;

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


  public void triggerSignUp(ActionEvent actionEvent) {
    try{
      User user = new User(signupUsername.getText(), signupPassword.getText(), signupName.getText(),
              signupNRIC.getText(), signupDOB.getValue());
      signupOutput.setText("");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      signupOutput.setText(e.getMessage());
    }

  }
}

