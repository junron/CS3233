package application;

import javafx.LineAnimation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
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
  private LineAnimation prevAnimation;

  public void initialize(Pane parent) {
    this.parent = parent;
  }

  public void startAnimation() {
    Ray r = Storage.rayTabController.getFocusedRay();
    CompletableFuture<ArrayList<Node>> future = r.renderRays(Storage.opticalRectangles);
    ArrayList<Point2D> points;
    try {
      ArrayList<Node> nodes = future.get();
      points = convertLineToPoints(nodes);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return;
    }
    if (prevAnimation != null) {
      parent.getChildren().removeAll(prevAnimation.getLines());
    }
    r.removeAllLines();
    LineAnimation lineAnimation = new LineAnimation(points.toArray(Point2D[]::new), pxRate, r.getColor(), parent);
    lineAnimation.start();
    prevAnimation = lineAnimation;
  }

  private ArrayList<Point2D> convertLineToPoints(ArrayList<Node> lines) {
    ArrayList<Point2D> result = new ArrayList<>();
    for (Node line : lines) {
      if (!(line instanceof PreciseLine)) continue;
      result.add(new Point2D(((Line) line).getStartX(), ((Line) line).getStartY()));
      result.add(new Point2D(((Line) line).getEndX(), ((Line) line).getEndY()));
    }
    return result;
  }
}
