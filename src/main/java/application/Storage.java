package application;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import networking.NetworkingClient;
import optics.light.Ray;
import optics.objects.OpticalRectangle;
import utils.OpticsList;

import java.util.ArrayList;
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

  static void setOffset(Point2D offset) {
    Storage.offset = offset;
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

  public static void updateRay(Ray r, int index) {
    Ray old = rays.get(index);
    r.clone(old);
  }

  public static void updateOpticalRectangle(OpticalRectangle rectangle, int index) {
    OpticalRectangle outGoing = opticalRectangles.get(index);
    rectangle.clone(outGoing);
  }

  public static void reRenderAll() {
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
    isMaximumDepthExceeded = false;
    parent.getChildren().removeAll(result);
    //  Add nodes
    parent.getChildren().addAll(result);
    return false;
  }

  public static void removeOptical(int i) {
    OpticalRectangle old = opticalRectangles.get(i);
    parent.getChildren().remove(old);
    opticalRectangles.remove(i);
  }

  public static void removeRay(int i) {
    Ray old = rays.get(i);
    old.destroy();
    rays.remove(i);
  }

  static void clearAll(boolean sync) {
    offset = new Point2D(0, 0);
    parent.getChildren().removeAll(opticalRectangles);
    if (sync) {
      for (int i = 0; i < opticalRectangles.size(); i++) {
        NetworkingClient.removeObject("", 0);
      }
      for (Ray r : rays) {
        NetworkingClient.removeObject("r", 0);
        r.destroy();
      }
    }
    opticalRectangles.clear();
    rays.clear();
    reRenderAll();
  }

  static void clearAll() {
    clearAll(true);
  }
}

