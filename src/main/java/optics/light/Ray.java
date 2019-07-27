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
import serialize.Serializable;
import utils.Geometry;
import utils.OpticsList;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Function;

public class Ray implements Lightsource, Serializable {
  private ArrayList<EventHandler<Event>> onStateChange = new ArrayList<>();
  private Function<Event, Void> onDestroy;
  private ArrayList<Line> lines = new ArrayList<>();
  private Line originalLine;
  private Line currentLine;
  private Point2D origin;
  private Point2D originalOrigin;
  private Point2D endPoint;
  private Pane parent;
  private double angle;
  private Circle circle;

  public Ray(Line l, Pane parent) {
    this.currentLine = l;
    this.originalLine = new Line(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY());
    this.origin = new Point2D(l.getStartX(), l.getStartY());
    this.originalOrigin = new Point2D(l.getStartX(), l.getStartY());
    this.endPoint = new Point2D(l.getEndX(), l.getEndY());
    this.angle = Math.toDegrees(Math.atan2(l.getEndY() - l.getStartY(), l.getEndX() - l.getStartX()));
    this.parent = parent;
    addCircle();
  }

  private void addCircle(){
    //    Circle at the start of the ray
    this.circle = new Circle(origin.getX(), origin.getY(), 6, Color.BLUE);
    circle.setFill(Color.rgb(255, 0, 0, 0.25));
    circle.setStroke(Color.BLACK);
    circle.setRotate(angle);
    new Rotatable(circle, e -> {
      this.angle = circle.getRotate();
      updateLine(circle.getRotate(), new Point2D(circle.getCenterX(), circle.getCenterY()));
      triggerStateChange(e);
    });
    new Draggable(circle, e -> {
      this.angle = circle.getRotate();
      updateLine(circle.getRotate(), new Point2D(circle.getCenterX(), circle.getCenterY()));
      triggerStateChange(e);
    }, this::triggerDestroy, parent);
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

  public void destroy(){
    parent.getChildren().remove(circle);
    this.removeAllLines();
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

  @Override
  public byte[] serialize() {
    //    x,y,rotation
    ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES * 2 + Integer.BYTES + Character.BYTES);
    byteBuffer.putChar('r');
    byteBuffer.putDouble(this.originalOrigin.getX());
    byteBuffer.putDouble(this.originalOrigin.getY());
    byteBuffer.putInt((int) this.angle);
    return byteBuffer.array();
  }

  @Override
  public void deserialize(byte[] serialized) {
    ByteBuffer buffer = ByteBuffer.wrap(serialized);
    double x = buffer.getDouble(Character.BYTES);
    double y = buffer.getDouble(Character.BYTES + Double.BYTES);
    this.angle = buffer.getInt(Character.BYTES + Double.BYTES * 2);
    updateLine(angle, new Point2D(x, y));
    addCircle();
  }
}
