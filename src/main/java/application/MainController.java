package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import optics.objects.Wall;

import java.net.URL;
import java.util.ResourceBundle;

import static application.Storage.opticalRectangles;
import static application.Storage.reRenderAll;

public class MainController implements Initializable {

  @FXML
  private AnchorPane parent;

  @FXML
  private RayTabController rayTabController;
  @FXML
  private GeneralTabController generalTabController;
  @FXML
  private OpticsTabController opticsTabController;
  @FXML
  private AnimationTabController animationTabController;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    rayTabController.initialize(parent);
    generalTabController.initialize(parent, opticsTabController, rayTabController);
    opticsTabController.initialize(parent);
    animationTabController.initialize(parent);
    Storage.opticsTabController = opticsTabController;
    Storage.rayTabController = rayTabController;
    Storage.parent = parent;
    parent.getChildren().addAll(opticalRectangles);
    Wall wall = new Wall(0, 0, 2500, 10, parent, 0);
    opticalRectangles.add(wall);
    parent.heightProperty().addListener((o, e, val) -> {
      wall.setY((double) val - 160);
      reRenderAll();
    });
  }
}

