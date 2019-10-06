package application;

import javafx.LineAnimation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import optics.light.Ray;
import serialize.Deserialize;
import serialize.FileOps;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static application.AnimationTabController.convertLineToPoints;

public class SplashScreenController implements Initializable {
  @FXML
  private ProgressBar progressBar;
  @FXML
  private Label status;
  @FXML
  private AnchorPane main;

  private ArrayList<String> messages = new ArrayList<>();
  private Pane prev;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //Random messages
    messages.add("Hacking NASA with HTML");
    messages.add("Making NSC memes");
    messages.add("Ditching Eclipse");
    messages.add("Solving p=np");
    messages.add("Catching all Pokemons");
    messages.add("Resolving dependencies");
    messages.add("Reloading OOP");
    messages.add("Extending classes");
    messages.add("Mining Bitcoins");
    Collections.shuffle(messages);
    //Legit messages
    messages.add(0, "Collecting telemetry");
    messages.add(0, "Connecting to servers");
    String[] out = new String[7];
    for (int i = 0; i < 7; i++) {
      out[i] = messages.get(i);
    }
    ArrayList<String> data;
    try {
      data = FileOps.load(new File(getClass().getResource("/logo.raysim").getFile()));
    } catch (IOException ex) {
      ex.printStackTrace();
      return;
    }
    for (String object : data) {
      Deserialize.deserializeAndAdd(object, main);
    }
    ArrayList<Node> remove = new ArrayList<>();
    Storage.parent.getChildren().forEach(child->{
      if(child instanceof Line || child instanceof GridPane){
        remove.add(child);
      }
    });
    Storage.parent.getChildren().removeAll(remove);
    Storage.setOffset(new Point2D(-75, -275));
    prev = Storage.parent;
    Storage.parent = main;
    startAnimation();
    Storage.showLabels = true;
    new Thread(() -> {
      for (int i = 0; i < 7; i++) {
        int finalI = i;
        Platform.runLater(() -> {
          status.setText(out[finalI]);
          progressBar.setProgress((finalI + 1.0) / 7);
        });
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private void startAnimation() {
    Storage.showLabels = false;
    Ray r = Storage.rays.get(0);
    Ray r2 = Storage.rays.get(1);
    CompletableFuture<ArrayList<Node>> future = r.renderRays(Storage.opticalRectangles.deepClone());
    CompletableFuture<ArrayList<Node>> future2 = r2.renderRays(Storage.opticalRectangles.deepClone());
    ArrayList<Point2D> points;
    ArrayList<Point2D> points2;
    try {
      ArrayList<Node> nodes = future.get();
      ArrayList<Node> nodes2 = future2.get();
      points = convertLineToPoints(nodes);
      points2 = convertLineToPoints(nodes2);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return;
    }
    r.removeAllLines();
    r2.removeAllLines();
    LineAnimation lineAnimation1 = animateRay(r, points);
    LineAnimation lineAnimation2 = animateRay(r2, points2);
    Storage.isAnimating = true;
    lineAnimation1.start();
    lineAnimation2.start();
  }

  private LineAnimation animateRay(Ray r, ArrayList<Point2D> points2) {
    return new LineAnimation(points2.toArray(Point2D[]::new), 300, r.getColor(), main, lineAnimation1 -> {
      main.getChildren().removeAll(lineAnimation1.getLines());
      Storage.showLabels = false;
      Storage.rerenderRay(r);
      Storage.showLabels = true;
      if(!Storage.isAnimating){
        Storage.parent = prev;
      }
      Storage.isAnimating = false;
      return null;
    });
  }
}
