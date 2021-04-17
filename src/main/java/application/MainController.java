package application;

import javafx.AngleDisplay;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import math.Vectors;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

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
  private Point2D movementDelta;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    rayTabController.initialize(parent);
    generalTabController.initialize(parent);
    opticsTabController.initialize(parent);
    animationTabController.initialize(parent);
    Storage.opticsTabController = opticsTabController;
    Storage.rayTabController = rayTabController;
    Storage.parent = parent;
    parent.setOnMousePressed(event -> {
      if (!Storage.isAnimating) movementDelta = new Point2D(event.getSceneX(), event.getSceneY());
    });
    parent.setOnMouseDragged(event -> {
      if (Storage.isAnimating) return;
      Point2D newOffset = new Point2D(event.getSceneX(), event.getSceneY()).subtract(movementDelta);
      Storage.setOffset(Storage.getOffset().add(newOffset));
      movementDelta = new Point2D(event.getSceneX(), event.getSceneY());
    });
    parent.setOnMouseMoved(event -> {
      Point2D coords = new Point2D(event.getSceneX(), event.getSceneY());
      ArrayList<Point2D> remove = new ArrayList<>();
      for (Map.Entry<Point2D, AngleDisplay> entry : Storage.intersectionPoints.entrySet()) {
        if (!parent.getChildren().contains(entry.getValue())) {
          remove.add(entry.getKey());
          continue;
        }
        if (Vectors.distanceSquared(entry.getKey(), coords) < 400) {
          entry.getValue().setVisible(true);
          entry.getValue().setLayoutX(event.getSceneX() + 7);
          entry.getValue().setLayoutY(event.getSceneY() + 7);
        } else {
          entry.getValue().setVisible(false);
        }
      }
      remove.forEach(point2D -> Storage.intersectionPoints.remove(point2D));
    });

  }
}

