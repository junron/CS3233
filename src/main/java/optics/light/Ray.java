package optics.light;

import application.FxAlerts;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import math.Intersection;
import math.Vectors;
import optics.objects.OpticalRectangle;
import utils.Geometry;
import utils.OpticsList;

import java.util.ArrayList;

public class Ray implements Lightsource {
  private ArrayList<Line> lines = new ArrayList<>();
  private Line originalLine;
  private Line currentLine;
  private Point2D origin;
  private Point2D originalOrigin;
  private Point2D endPoint;
  private Pane parent;

  public Ray(Line l, Pane parent) {
    this.currentLine = l;
    this.originalLine = new Line(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY());
    this.origin = new Point2D(l.getStartX(), l.getStartY());
    this.originalOrigin = new Point2D(l.getStartX(), l.getStartY());
    this.endPoint = new Point2D(l.getEndX(), l.getEndY());
    this.parent = parent;
  }

  @Override
  public void renderRays(OpticsList objects) {
    this.resetCurrentLine();
    this.removeAllLines();
    OpticalRectangle opticalObject = Geometry.getNearestIntersection(this.currentLine, objects);
    int refNum = 0;
    while (opticalObject != null) {
      if (refNum > this.maximumReflectionDepth) {
        System.out.println("Maximum reflection depth exceeded");
        Alert alert = FxAlerts
                .showErrorDialog("Error", "Outstanding move, but that's illegal", "Maximum reflection depth exceeded");
        alert.showAndWait();
        break;
      }
      Path intersection = (Path) Shape.intersect(this.currentLine, opticalObject);
      Point2D iPoint = Intersection.getIntersectionPoint(intersection, new Vectors(origin));
      Line transform = opticalObject.transform(this.currentLine, iPoint);
      Line normal = opticalObject.drawNormal(opticalObject.getIntersectionSideData(iPoint), iPoint);
      parent.getChildren().addAll(this.currentLine, normal);
      lines.add(this.currentLine);
      lines.add(normal);
      this.currentLine = transform;
      this.origin = iPoint;
      opticalObject = Geometry.getNearestIntersection(this.currentLine, objects.getAllExcept(opticalObject));
      refNum++;
    }
    parent.getChildren().add(this.currentLine);
    lines.add(this.currentLine);
  }

  private void resetCurrentLine() {
    this.originalLine.setEndX(endPoint.getX());
    this.originalLine.setEndY(endPoint.getY());
    this.origin = this.originalOrigin;
    this.currentLine = this.originalLine;
  }

  @Override
  public void removeAllLines() {
    this.parent.getChildren().removeAll(lines);
  }
}
