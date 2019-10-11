package application;

import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
  public AnchorPane parent;


  public void createObject() {
    BlackCircle blackCircle = new BlackCircle(parent.getWidth() / 2, parent.getHeight() / 2);
    parent.getChildren().add(blackCircle);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Movable.init(parent);
  }
}
