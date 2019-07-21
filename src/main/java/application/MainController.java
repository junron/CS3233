package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import math.Intersection;
import math.Vectors;
import optics.Mirror;
import optics.OpticalRectangle;
import utils.FxDebug;
import utils.Geometry;
import utils.OpticsList;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

  private Line line;
  private OpticsList<OpticalRectangle> mirrors = new OpticsList<>();
  private int maximumReflectionDepth = 100;

  @FXML
  private AnchorPane parent;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Point2D origin = new Point2D(300, 10);
    FxDebug.indicatePoint(origin, Color.GREEN,parent);
    double angleDeg = 90;
    Mirror m = new Mirror(300, 100, 14, 200, 0);
//    Mirror m4 = new Mirror(200, 200, 14, 200, 90);
    mirrors.addAll(m);
    parent.getChildren().addAll(mirrors);
    this.line = Geometry
            .createLineFromPoints(origin, origin.add(Vectors.constructWithMagnitude(Math.toRadians(angleDeg), 1000)));
    OpticalRectangle opticalObject = Geometry.getNearestIntersection(this.line, mirrors);
    int refNum = 0;
    while (opticalObject != null) {
      if(refNum>this.maximumReflectionDepth) {
        System.out.println("Maximum reflection depth exceeded");
        break;
      }
      Path intersection = (Path) Shape.intersect(this.line,opticalObject);
      Point2D iPoint = Intersection.getIntersectionPoint(intersection, new Vectors(origin));
      Line transform = opticalObject.transform(this.line, iPoint);
      Line normal = opticalObject.drawNormal(opticalObject.getIntersectionSideData(iPoint), iPoint);
      parent.getChildren().addAll(this.line,normal);
      this.line = transform;
      opticalObject = Geometry.getNearestIntersection(this.line, mirrors.getAllExcept(opticalObject));
      refNum++;
    }
    parent.getChildren().add(this.line);
  }
}

