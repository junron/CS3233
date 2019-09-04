package application;

import javafx.scene.Node;
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
  private static CompletableFuture<Void> allFuture;

  static void reRenderAll() {
    if (allFuture != null) {
      System.out.println("Cancelled");
      allFuture.cancel(true);
    }
    ArrayList<CompletableFuture<ArrayList<ArrayList<Node>>>> futures = new ArrayList<>();
    for (Ray r : rays) {
      futures.add(r.renderRays(opticalRectangles));
    }
    allFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    allFuture.join();
    for (CompletableFuture<ArrayList<ArrayList<Node>>> future : futures) {
      try {
        ArrayList<ArrayList<Node>> result = future.get();
        //        Remove old lines
//        parent.getChildren().removeAll(result.get(1));
//        //        Add nodes
//        parent.getChildren().addAll(result.get(0));
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
    allFuture = null;
  }

  public static OpticsTabController opticsTabController;
  public static RayTabController rayTabController;
  public static int maximumReflectionDepth = 1000;
}
