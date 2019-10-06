package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
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
  @FXML
  private AnimationTabController animationTabController;
  @FXML
  private ServerTabController collabTabController;
  private Point2D movementDelta;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    rayTabController.initialize(parent);
    generalTabController.initialize(parent, opticsTabController, rayTabController);
    opticsTabController.initialize(parent);
    animationTabController.initialize(parent);
    collabTabController.initialize(parent);
    Storage.opticsTabController = opticsTabController;
    Storage.rayTabController = rayTabController;
    Storage.parent = parent;
    parent.getChildren().addAll(opticalRectangles);
    parent.setOnMousePressed(event -> {
      if (!Storage.isAnimating) movementDelta = new Point2D(event.getSceneX(), event.getSceneY());
    });
    parent.setOnMouseDragged(event -> {
      if (Storage.isAnimating) return;
      Point2D newOffset = new Point2D(event.getSceneX(), event.getSceneY()).subtract(movementDelta);
      Storage.setOffset(Storage.getOffset().add(newOffset));
      movementDelta = new Point2D(event.getSceneX(), event.getSceneY());
    });
  }
}

