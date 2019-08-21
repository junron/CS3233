package application;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import models.Admin;
import models.User;

import static storage.UserStorage.storage;

public class MainController {

  @FXML
  private TextField signinUsername;

  @FXML
  private PasswordField signinPassword;

  @FXML
  private Text signinOutput;


  public void triggerSignIn() {
    User user = storage.getUserByUsername(signinUsername.getText());
    if (user == null) {
      signinOutput.setText("User not found");
      return;
    }
    if (user.signIn(signinPassword.getText())) {
      signinOutput.setText("Signed in successfully");
      if (user instanceof Admin) {
        System.out.println("Admin");
        ScreenController.activate("admin");
        return;
      }
      GalleryController.setUser(user);
      ScreenController.activate("gallery");
      signinOutput.setFill(Color.GREEN);
    } else {
      signinOutput.setText("Incorrect password");
    }
  }

  public void openSignUp() {
    ScreenController.activate("signup");
  }
}

