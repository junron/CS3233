package optics.light;

import application.Storage;
import javafx.AngleDisplay;
import javafx.Draggable;
import javafx.Rotatable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import math.Intersection;
import math.Vectors;
import optics.PreciseLine;
import optics.TransformData;
import optics.objects.OpticalRectangle;
import optics.objects.Refract;
import serialize.Serializable;
import utils.Geometry;
import utils.OpticsList;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Ray implements LightSource, Serializable {
  private ArrayList<EventHandler<Event>> onStateChange = new ArrayList<>();
  private Function<Event, Void> onDestroy;
  private Function<Boolean, Void> onFocusStateChanged;
  private ArrayList<Node> lines = new ArrayList<>();
  private PreciseLine originalLine;
  private PreciseLine currentLine;
  private Point2D origin;
  private Point2D originalOrigin;
  private Point2D endPoint;
  private Pane parent;
  private double angle;
  private Circle circle;
  private Color color;
  private boolean inRefractiveMaterial;


  public Ray(PreciseLine l, Pane parent) {
    this.currentLine = l;
    this.originalLine = new PreciseLine(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY());
    this.originalLine.setPreciseAngle(l.getPreciseAngle());
    this.origin = new Point2D(l.getStartX(), l.getStartY());
    this.originalOrigin = new Point2D(l.getStartX(), l.getStartY());
    this.endPoint = new Point2D(l.getEndX(), l.getEndY());
    this.angle = Math.toDegrees(l.getPreciseAngle());
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

  public PreciseLine getCurrentLine() {
    return currentLine;
  }

  public ArrayList<Node> getLines() {
    return lines;
  }


  public boolean isInRefractiveMaterial() {
    return inRefractiveMaterial;
  }

  public void setInRefractiveMaterial(boolean inRefractiveMaterial) {
    this.inRefractiveMaterial = inRefractiveMaterial;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Circle getCircle() {
    return circle;
  }

  public void setOnFocusStateChanged(Function<Boolean, Void> onFocusStateChanged) {
    this.onFocusStateChanged = onFocusStateChanged;
  }

  private void addCircle() {
    if (this.circle != null) this.parent.getChildren().remove(circle);
    //    Circle at the start of the ray
    this.circle = new RayCircle(this.origin.getX(), this.origin.getY(), this.angle, this);
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
    PreciseLine l = new PreciseLine(startPoint.getX(), startPoint.getY(), 0, 0);
    l.setPreciseAngle(Math.toRadians(rotation));
    Vectors lineVec = Vectors.constructWithMagnitude(l.getPreciseAngle(), 2500);
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


  private ObservableList<Node> renderRaysThreaded(OpticsList<OpticalRectangle> objects) {
    ObservableList<Node> nodes = FXCollections.observableArrayList();
    this.resetCurrentLine();
    OpticalRectangle opticalObject = Geometry.getNearestIntersection(this.currentLine, objects);
    int refNum = 0;
    while (opticalObject != null) {
      if (refNum > Storage.maximumReflectionDepth) {
        return null;
      }
      this.currentLine.setStroke(this.color);
      Path intersection = (Path) Shape.intersect(this.currentLine, opticalObject);
      Point2D iPoint = Intersection.getIntersectionPoint(intersection, new Vectors(origin), !Intersection
              .hasIntersectionPoint(this.origin, opticalObject));

      TransformData transform = opticalObject.transform(this, iPoint);
      //        End of line
      if (transform == null) break;

      Line normal = opticalObject.drawNormal(transform.getIntersectionSideData(), iPoint);
      Circle activeArea = new Circle(iPoint.getX(), iPoint.getY(), 20, Color.color(0, 0, 0, 0));
      AngleDisplay angleDisplay = transform.getAngleDisplay();
      angleDisplay.setVisible(false);
      activeArea.setOnMouseEntered(event -> {
        angleDisplay.setLayoutX(event.getSceneX() + 7);
        angleDisplay.setLayoutY(event.getSceneY() + 7);
        angleDisplay.setVisible(true);
      });
      activeArea.setOnMouseMoved(event -> {
        angleDisplay.setLayoutX(event.getSceneX() + 7);
        angleDisplay.setLayoutY(event.getSceneY() + 7);
      });
      activeArea.setOnMouseExited(event -> angleDisplay.setVisible(false));
      //      Find next optical object for light to interact with
      //Can interact with current object if currently inside object and is refractor
      OpticalRectangle nextOpticalObject = opticalObject instanceof Refract && this.isInRefractiveMaterial() ? Geometry
              .getNearestIntersection(transform.getPreciseLine(), objects, opticalObject) : Geometry
              .getNearestIntersection(transform.getPreciseLine(), objects.getAllExcept(opticalObject));
      //        Final check for bugs
      //        Detect if ray is passing through an optical object (rays cant pass through mirrors)
      //        If detected, stop further rendering
      if (nextOpticalObject == null) {
        for (OpticalRectangle object : objects) {
          if (object == opticalObject) continue;
          if (Intersection.hasExitPoint(Shape.intersect(transform.getPreciseLine(), object), new Point2D(transform
                  .getPreciseLine().getStartX(), transform.getPreciseLine().getStartY()))) {
            if (nodes.size() == 0) {
              //              Ensure that there is at least 1 line
              lines.add(this.currentLine);
              nodes.add(this.currentLine);
            }
            //  Abort rendering
            this.inRefractiveMaterial = false;
            lines.addAll(nodes);
            return nodes;
          }
        }
      }
      //      Let next optical object be current optical object
      opticalObject = nextOpticalObject;
      nodes.addAll(this.currentLine, normal, angleDisplay, activeArea);
      this.currentLine = transform.getPreciseLine();
      this.origin = iPoint;
      refNum++;
    }
    this.currentLine.setStroke(this.color);
    nodes.add(this.currentLine);
    lines.addAll(nodes);
    this.inRefractiveMaterial = false;
    return nodes;
  }

  private void resetCurrentLine() {
    this.originalLine.setEndX(endPoint.getX());
    this.originalLine.setEndY(endPoint.getY());
    this.origin = this.originalOrigin;
    this.currentLine = this.originalLine;
  }

  @Override
  public CompletableFuture<ArrayList<Node>> renderRays(OpticsList<OpticalRectangle> objects) {
    CompletableFuture<ArrayList<Node>> completableFuture = new CompletableFuture<>();
    new Thread(() -> {
      ObservableList<Node> nodes;
      try {
        nodes = this.renderRaysThreaded(objects);
      } catch (Exception e) {
        e.printStackTrace();
        completableFuture.completeExceptionally(e);
        return;
      }
      completableFuture.complete(nodes == null ? null : new ArrayList<>(nodes));
    }).start();
    return completableFuture;
  }

  @Override
  public void removeAllLines() {
    final Node[] lines = this.lines.toArray(new Node[0]);
    this.lines.clear();
    Platform.runLater(() -> this.parent.getChildren().removeAll(lines));
  }

  @Override
  public byte[] serialize() {
    //    x,y,rotation,r,g,b
    ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES * 3 + Character.BYTES + Double.BYTES * 3);
    byteBuffer.putChar('r');
    byteBuffer.putDouble(this.originalOrigin.getX());
    byteBuffer.putDouble(this.originalOrigin.getY());
    byteBuffer.putDouble(this.angle);
    byteBuffer.putDouble(this.color.getRed());
    byteBuffer.putDouble(this.color.getGreen());
    byteBuffer.putDouble(this.color.getBlue());
    return byteBuffer.array();
  }

  @Override
  public void deserialize(byte[] serialized) {
    ByteBuffer buffer = ByteBuffer.wrap(serialized);
    buffer.getChar();
    double x = buffer.getDouble();
    double y = buffer.getDouble();
    this.angle = buffer.getDouble();
    double red = buffer.getDouble();
    double green = buffer.getDouble();
    double blue = buffer.getDouble();
    updateLine(angle, new Point2D(x, y));
    parent.getChildren().removeAll(this.circle);
    addCircle();
    this.setColor(Color.rgb((int) (red * 255), (int) (green * 255), (int) (blue * 255)));
  }

  Ray clone(boolean move) {
    Point2D add = move ? new Point2D(10, 10) : new Point2D(0, 0);
    PreciseLine l = new PreciseLine(Geometry.createLineFromPoints(this.originalOrigin.add(add), Vectors
            .constructWithMagnitude(this.originalLine.getPreciseAngle(), 2500).add(this.originalOrigin).add(add)));
    l.setPreciseAngle(this.originalLine.getPreciseAngle());
    Ray res = new Ray(l, parent);
    res.setColor(this.color);
    return res;
  }
}
