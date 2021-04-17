package application;

import javafx.AngleDisplay;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import optics.light.Ray;
import optics.objects.OpticalRectangle;
import utils.OpticsList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Storage {
  static final ArrayList<Ray> rays = new ArrayList<>();
  static final OpticsList<OpticalRectangle> opticalRectangles = new OpticsList<>();
  public static OpticsTabController opticsTabController;
  public static RayTabController rayTabController;
  public static int maximumReflectionDepth = 1000;
  public static boolean showLabels = true;
  public static boolean isAnimating = false;
  static boolean darkTheme = false;
  static Pane parent;
  private static boolean isMaximumDepthExceeded = false;
  private static Point2D offset = new Point2D(0, 0);
  static Map<Point2D, AngleDisplay> intersectionPoints = new HashMap<>();
  private static long prevRender = 0;

  static void setOffset(Point2D offset) {
    Storage.offset = offset;
    if ((System.currentTimeMillis() - prevRender <= 100)) {
      return;
    }
    prevRender = System.currentTimeMillis();
    opticalRectangles.forEach(OpticalRectangle::reposition);
    rays.forEach(ray -> {
      ray.reposition();
      rerenderRay(ray);
    });
  }

  public static Point2D getOffset() {
    return offset;
  }

  static void rerenderRay(Ray ray) {
    CompletableFuture<ArrayList<Node>> future = ray.renderRays(opticalRectangles.deepClone());
    //Remove old lines
    ray.removeAllLines();
    handleRender(future);
  }

  public static void reRenderAll() {
    if ((System.currentTimeMillis() - prevRender <= 10)) {
      return;
    }
    prevRender = System.currentTimeMillis();
    ArrayList<CompletableFuture<ArrayList<Node>>> futures = new ArrayList<>();
    ArrayList<Node> lines = new ArrayList<>();
    for (Ray r : rays) {
      lines.addAll(r.getLines());
      futures.add(r.renderRays(opticalRectangles.deepClone()));
    }
    //Remove old lines
    parent.getChildren().removeAll(lines);
    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

    //    Block operations
    voidCompletableFuture.join();

    for (CompletableFuture<ArrayList<Node>> future : futures) {
      if (handleRender(future)) break;
    }

  }

  private static boolean handleRender(CompletableFuture<ArrayList<Node>> future) {
    ArrayList<Node> result;
    try {
      result = future.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return true;
    }
    if (result == null) {
      //          Cancel extra alerts
      if (isMaximumDepthExceeded) return true;
      isMaximumDepthExceeded = true;
      Alert alert = FxAlerts
              .showErrorDialog("Error", "Outstanding move, but that's illegal", "Maximum reflection depth " +
                      "exceeded");
      alert.showAndWait();
      return true;
    }
    ArrayList<Node> finalResults = new ArrayList<>();
    Node prev = null;
    for (Node node : result) {
      if (node instanceof Circle && prev instanceof AngleDisplay) {
        intersectionPoints
                .put(new Point2D(((Circle) node).getCenterX(), ((Circle) node).getCenterY()), (AngleDisplay) prev);
      } else {
        node.setMouseTransparent(true);
        finalResults.add(node);
      }
      prev = node;
    }
    isMaximumDepthExceeded = false;
    //  Add nodes
    parent.getChildren().addAll(finalResults);
    return false;
  }

  static void clearAll() {
    offset = new Point2D(0, 0);
    parent.getChildren().removeAll(opticalRectangles);
    for (Ray r : rays) {
      r.destroy();
    }
    opticalRectangles.clear();
    rays.clear();
    reRenderAll();
  }
}

