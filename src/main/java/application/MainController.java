package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import math.Vectors;
import optics.light.Ray;
import optics.objects.Mirror;
import optics.objects.OpticalRectangle;
import utils.FxDebug;
import utils.Geometry;
import utils.OpticsList;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

  private ArrayList<Ray> rays = new ArrayList<>();
  private OpticsList<OpticalRectangle> mirrors = new OpticsList<>();

  @FXML
  private AnchorPane parent;

  @FXML
  private Button newMirror;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    double angle = Math.toRadians(30);
    Point2D origin = new Point2D(200, 100);
    FxDebug.indicatePoint(origin, Color.GREEN, parent);
    Line l = Geometry.createLineFromPoints(origin, Vectors.constructWithMagnitude(angle, 1000));
    Ray r1 = new Ray(l, parent);
    rays.add(r1);
    newMirror.setOnMouseClicked(event -> {
      Mirror m = new Mirror(300, 100, 14, 200, parent, 0);
      this.mirrors.add(m);
      m.addOnStateChange(event1 -> r1.renderRays(mirrors));
      m.setOnDestroy(e -> {
        mirrors.remove(m);
        r1.renderRays(mirrors);
        return null;
      });
      r1.renderRays(mirrors);
      parent.getChildren().add(m);
    });
    r1.renderRays(mirrors);
    parent.getChildren().addAll(mirrors);
  }
}

