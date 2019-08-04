package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

import static application.Storage.opticalRectangles;

public class MainController implements Initializable {

  @FXML
  private AnchorPane parent;

  @FXML
  private RayTabController rayTabController;
  @FXML
  private GeneralTabController generalTabController;
  @FXML
  private OpticsTabController opticsTabController;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    rayTabController.initialize(parent);
    generalTabController.initialize(parent,opticsTabController,rayTabController);
    opticsTabController.initialize(parent);
    Storage.opticsTabController = opticsTabController;
    Storage.rayTabController = rayTabController;
    parent.getChildren().addAll(opticalRectangles);
  }
}

