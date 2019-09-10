package application;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import optics.light.Ray;
import optics.objects.OpticalRectangle;
import utils.OpticsList;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Storage {
  static Pane parent;
  static final ArrayList<Ray> rays = new ArrayList<>();
  static final OpticsList<OpticalRectangle> opticalRectangles = new OpticsList<>();
  private static boolean isMaximumDepthExceeded = false;

  static void reRenderAll() {
    ArrayList<CompletableFuture<ArrayList<ArrayList<Node>>>> futures = new ArrayList<>();
    for (Ray r : rays) {
      futures.add(r.renderRays(opticalRectangles.deepClone()));
    }

    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

    //    Block operations
    voidCompletableFuture.join();


    for (CompletableFuture<ArrayList<ArrayList<Node>>> future : futures) {
      try {
        ArrayList<ArrayList<Node>> result = future.get();

        //  Remove old lines
        parent.getChildren().removeAll(result.get(1));

        //        Reflection depth exceeded
        if (result.get(0) == null) {
          //          Cancel extra alerts
          if (isMaximumDepthExceeded) break;
          isMaximumDepthExceeded = true;
          Alert alert = FxAlerts
                  .showErrorDialog("Error", "Outstanding move, but that's illegal", "Maximum reflection depth " +
                          "exceeded");
          alert.showAndWait();
          break;
        }
        isMaximumDepthExceeded = false;
        parent.getChildren().removeAll(result.get(0));
        //  Add nodes
        parent.getChildren().addAll(result.get(0));
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
  }

  public static OpticsTabController opticsTabController;
  public static RayTabController rayTabController;
  public static int maximumReflectionDepth = 1000;
}
