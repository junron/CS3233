package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import models.User;

import static storage.UserStorage.storage;

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
    try{
      User user = new User(signupUsername.getText(), signupPassword.getText(), signupName.getText(),
              signupNRIC.getText(), signupDOB.getValue());
      if(storage.getUserByUsername(signupUsername.getText())!=null){
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
    if(user==null){
      signinOutput.setText("User not found");
      return;
    }
    if(user.signIn(signinPassword.getText())){
      signinOutput.setText("Signed in successfully");
      signinOutput.setFill(Color.GREEN);
    }else{
      signinOutput.setText("Incorrect password");
    }
  }
}

