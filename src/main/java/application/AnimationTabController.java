package application;

import javafx.LineAnimation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import optics.PreciseLine;
import optics.light.Ray;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AnimationTabController {
  public TextField pixelsPerSecond;
  private int pxRate = 300;
  private Pane parent;
  private LineAnimation currentAnimation;

  public void initialize(Pane parent) {
    this.parent = parent;
    pixelsPerSecond.textProperty().addListener((o, k, val) -> {
      int pxRate;
      try {
        pxRate = Integer.parseInt(val);
      } catch (NumberFormatException e) {
        return;
      }
      this.pxRate = pxRate;
      if (this.currentAnimation != null) {
        this.currentAnimation.setPxRate(this.pxRate);
      }
    });
  }

  public void startAnimation() {
    Ray r = Storage.rayTabController.getFocusedRay();
    if (r == null) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Animation error");
      alert.setHeaderText("No ray selected");
      alert.setContentText("Please select a ray to animate");
      alert.showAndWait();
      return;
    }
    CompletableFuture<ArrayList<Node>> future = r.renderRays(Storage.opticalRectangles.deepClone());
    ArrayList<Point2D> points;
    try {
      ArrayList<Node> nodes = future.get();
      if (nodes == null) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Animation error");
        alert.setHeaderText("Cannot animate to infinite");
        alert.setContentText("Please resolve maximum reflection depth exceeded errors\n before animating");
        alert.showAndWait();
        return;
      }
      points = convertLineToPoints(nodes);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return;
    }
    r.removeAllLines();
    LineAnimation lineAnimation = new LineAnimation(points.toArray(Point2D[]::new), pxRate, r
            .getColor(), parent, lineAnimation1 -> {
      parent.getChildren().removeAll(lineAnimation1.getLines());
      Storage.isAnimating = false;
      currentAnimation = null;
      Storage.rerenderRay(r);
      return null;
    });
    //Lock movement while animating
    Storage.isAnimating = true;
    currentAnimation = lineAnimation;
    lineAnimation.start();
  }

  static ArrayList<Point2D> convertLineToPoints(ArrayList<Node> lines) {
    ArrayList<Point2D> result = new ArrayList<>();
    for (Node line : lines) {
      if (!(line instanceof PreciseLine)) continue;
      result.add(new Point2D(((Line) line).getStartX(), ((Line) line).getStartY()));
      result.add(new Point2D(((Line) line).getEndX(), ((Line) line).getEndY()));
    }
    return result;
  }
}
