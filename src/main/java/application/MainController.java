package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import optics.objects.Mirror;
import optics.objects.Wall;

import java.net.URL;
import java.util.ResourceBundle;

import static application.Storage.addObject;
import static application.Storage.opticalRectangles;

public class MainController implements Initializable {

  @FXML
  private AnchorPane parent;

  @FXML
  private Button newMirror;
  @FXML
  private Button newWall;

  @FXML
  private RayTabController rayTabController;
  @FXML
  private GeneralTabController generalTabController;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    newMirror.setOnMouseClicked(event -> {
      Mirror m = new Mirror(parent.getWidth()/2, parent.getHeight()/2, 20, 200, parent, 0);
      addObject(m,parent);
    });
    newWall.setOnMouseClicked(event -> {
      Wall w = new Wall(parent.getWidth()/2, parent.getHeight()/2, 20, 50, parent, 0);
      addObject(w,parent);
    });
    rayTabController.initialize(parent);
    generalTabController.initialize(parent);
    parent.getChildren().addAll(opticalRectangles);
  }
}

