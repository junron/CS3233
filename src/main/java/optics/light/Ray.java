package optics.light;

import application.FxAlerts;
import javafx.Draggable;
import javafx.Rotatable;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import math.Intersection;
import math.Vectors;
import optics.objects.OpticalRectangle;
import utils.Geometry;
import utils.OpticsList;

import java.util.ArrayList;
import java.util.function.Function;

public class Ray implements Lightsource {
  private ArrayList<EventHandler<Event>> onStateChange = new ArrayList<>();
  private Function<Event, Void> onDestroy;
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
    double angle = Math.toDegrees(Math.atan2(l.getEndY() - l.getStartY(), l.getEndX() - l.getStartX()));
//    Circle at the end of the ray
    Circle circle = new Circle(origin.getX(), origin.getY(), 6, Color.BLUE);
    circle.setFill(Color.rgb(255, 0, 0, 0.25));
    circle.setStroke(Color.BLACK);
    circle.setRotate(angle);
//    Account for difference between getLayoutX and getX
    Point2D difference = new Point2D(circle.getLayoutX(), circle.getLayoutY()).subtract(origin);
    Rotatable r = new Rotatable(circle, e -> {
      updateLine(circle.getRotate(), new Point2D(circle.getLayoutX(), circle.getLayoutY()).subtract(difference));
      triggerStateChange(e);
    });
    Draggable d = new Draggable(circle, e -> {
      updateLine(circle.getRotate(), new Point2D(circle.getLayoutX(), circle.getLayoutY()).subtract(difference));
      triggerStateChange(e);
    }, this::triggerDestroy, parent);
    this.parent = parent;
    this.parent.getChildren().add(circle);
  }

  public void addOnStateChange(EventHandler<Event> handler) {
    this.onStateChange.add(handler);
  }

  private void triggerStateChange(Event e) {
    for (EventHandler<Event> handler : this.onStateChange) {
      handler.handle(e);
    }
  }

  public void setOnDestroy(Function<Event, Void> onDestroy) {
    this.onDestroy = onDestroy;
  }

  private void triggerDestroy(Event e) {
    this.removeAllLines();
    this.onDestroy.apply(e);
  }

  private void updateLine(double rotation, Point2D startPoint) {
    this.origin = startPoint;
    this.originalOrigin = startPoint;
    Line l = new Line(startPoint.getX(), startPoint.getY(), 0, 0);
    Vectors lineVec = Vectors.constructWithMagnitude(Math.toRadians(rotation), 2500);
    Point2D endpoint = startPoint.add(lineVec);
    l.setEndX(endpoint.getX());
    l.setEndY(endpoint.getY());
    this.endPoint = endpoint;
    this.currentLine = l;
    this.originalLine = l;
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
