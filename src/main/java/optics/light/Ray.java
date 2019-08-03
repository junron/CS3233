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
import optics.objects.Refract;
import serialize.Serializable;
import utils.Geometry;
import utils.OpticsList;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Function;

public class Ray implements LightSource, Serializable {
  private ArrayList<EventHandler<Event>> onStateChange = new ArrayList<>();
  private Function<Event, Void> onDestroy;
  private Function<Boolean, Void> onFocusStateChanged;
  private ArrayList<Line> lines = new ArrayList<>();
  private Line originalLine;
  private Line currentLine;
  private Point2D origin;
  private Point2D originalOrigin;
  private Point2D endPoint;
  private Pane parent;
  private double angle;
  private Circle circle;
  private Color color;
  private double currentRefractiveIndex = 1;

  public Ray(Line l, Pane parent) {
    this.currentLine = l;
    this.originalLine = new Line(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY());
    this.origin = new Point2D(l.getStartX(), l.getStartY());
    this.originalOrigin = new Point2D(l.getStartX(), l.getStartY());
    this.endPoint = new Point2D(l.getEndX(), l.getEndY());
    this.angle = Math.toDegrees(Math.atan2(l.getEndY() - l.getStartY(), l.getEndX() - l.getStartX()));
    this.parent = parent;
    this.color = Color.BLACK;
    addCircle();
  }

  public double getAngle() {
    return angle;
  }

  public void setAngle(double angle) {
    updateLine(angle, this.originalOrigin);
    circle.setRotate(angle);
    this.angle = angle;
  }

  public Line getCurrentLine() {
    return currentLine;
  }

  public double getCurrentRefractiveIndex() {
    return currentRefractiveIndex;
  }

  public void setCurrentRefractiveIndex(double currentRefractiveIndex) {
    this.currentRefractiveIndex = currentRefractiveIndex;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }


  public void setOnFocusStateChanged(Function<Boolean, Void> onFocusStateChanged) {
    this.onFocusStateChanged = onFocusStateChanged;
  }

  private void addCircle() {
    //    Circle at the start of the ray
    this.circle = new Circle(origin.getX(), origin.getY(), 6, Color.BLUE);
    circle.setFill(Color.rgb(255, 0, 0, 0.25));
    circle.setStroke(Color.BLACK);
    circle.setRotate(angle);
    circle.focusedProperty().addListener((o, ol, state) -> onFocusStateChanged.apply(state));
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

  public void destroy() {
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

  public void requestFocus() {
    this.circle.requestFocus();
  }

  @Override
  public void renderRays(OpticsList<OpticalRectangle> objects) {
    this.resetCurrentLine();
    this.removeAllLines();
//    System.out.println(this.getCurrentRefractiveIndex());
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
      this.currentLine.setStroke(this.color);
      Path intersection = (Path) Shape.intersect(this.currentLine, opticalObject);
      Point2D iPoint = Intersection.getIntersectionPoint(intersection, new Vectors(origin), !Intersection
              .hasIntersectionPoint(this.origin, opticalObject));
      Line transform = opticalObject.transform(this, iPoint);
      if (transform == null) {
//        End of line
        break;
      }
      Line normal = opticalObject.drawNormal(opticalObject.getIntersectionSideData(iPoint), iPoint);
      parent.getChildren().addAll(this.currentLine, normal);
      lines.add(this.currentLine);
      lines.add(normal);
      this.currentLine = transform;
      this.origin = iPoint;
      opticalObject = opticalObject instanceof Refract ?
              Geometry.getNearestIntersection(this.currentLine, objects, opticalObject)
              : Geometry.getNearestIntersection(this.currentLine, objects.getAllExcept(opticalObject));
      refNum++;
    }
    this.currentLine.setStroke(this.color);
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
    lines.clear();
  }

  @Override
  public byte[] serialize() {
    //    x,y,rotation,r,g,b
    ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES * 2 + Integer.BYTES + Character.BYTES + Double.BYTES * 3);
    byteBuffer.putChar('r');
    byteBuffer.putDouble(this.originalOrigin.getX());
    byteBuffer.putDouble(this.originalOrigin.getY());
    byteBuffer.putInt((int) this.angle);
    byteBuffer.putDouble(this.color.getRed());
    byteBuffer.putDouble(this.color.getGreen());
    byteBuffer.putDouble(this.color.getBlue());
    return byteBuffer.array();
  }

  @Override
  public void deserialize(byte[] serialized) {
    ByteBuffer buffer = ByteBuffer.wrap(serialized);
    double x = buffer.getDouble(Character.BYTES);
    double y = buffer.getDouble(Character.BYTES + Double.BYTES);
    this.angle = buffer.getInt(Character.BYTES + Double.BYTES * 2);
    double red = buffer.getDouble(Character.BYTES + Integer.BYTES + Double.BYTES * 2);
    double green = buffer.getDouble(Character.BYTES + Integer.BYTES + Double.BYTES * 3);
    double blue = buffer.getDouble(Character.BYTES + Integer.BYTES + Double.BYTES * 4);
    updateLine(angle, new Point2D(x, y));
    addCircle();
    this.setColor(Color.rgb((int) (red * 255), (int) (green * 255), (int) (blue * 255)));
  }
}
