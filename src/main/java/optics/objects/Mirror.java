package optics.objects;

import javafx.AngleDisplay;
import javafx.Draggable;
import javafx.Rotatable;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import math.Intersection;
import math.IntersectionSideData;
import math.Vectors;
import optics.PreciseLine;
import optics.TransformData;
import optics.light.Ray;
import utils.Geometry;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Function;

public class Mirror extends OpticalRectangle {
  private ArrayList<EventHandler<Event>> onStateChange = new ArrayList<>();
  private Function<Event, Void> onDestroy;

  public Mirror(double x, double y, double width, double height, Pane parent, double rotation) {
    super(x, y, width, height);
    this.setRotate(rotation);
    this.setArcHeight(0);
    this.setArcWidth(0);
    this.setFill(Color.color(5 / 255.0, 213 / 255.0, 255 / 255.0, 0.28));
    this.setStrokeWidth(1);
    this.setStroke(Color.BLACK);
    this.parent = parent;
    new Draggable(this, this::triggerStateChange, this::triggerDestroy, parent);
    new Rotatable(this, this::triggerStateChange);
  }

  public void addOnStateChange(EventHandler<Event> handler) {
    this.onStateChange.add(handler);
  }

  private void triggerStateChange(Event e) {
    for (EventHandler<Event> handler : this.onStateChange) {
      handler.handle(e);
    }
  }

  private void triggerDestroy(Event e) {
    this.onDestroy.apply(e);
  }

  public void setOnDestroy(Function<Event, Void> onDestroy) {
    this.onDestroy = onDestroy;
  }

  @Override
  public Interactive cloneObject() {
    return this.clone(false);
  }

  @Override
  public TransformData transform(Ray r, Point2D iPoint) {
    PreciseLine l = r.getCurrentLine();
    l.setEndX(iPoint.getX());
    l.setEndY(iPoint.getY());
    IntersectionSideData iData = getIntersectionSideData(iPoint, new Point2D(l.getStartX(), l.getStartY()), null);
    if (iData == null || iData.normalVector == null) {
      System.out.println("ERRORORROOROR: iData is null");
      return null;
    }
    double normalAngle = iData.normalAngle;
    double intersectionAngle = Intersection.getObjectIntersectionAngle(iData, l);
    Line newLine = Geometry.createLineFromPoints(iPoint, iPoint
            .add(Vectors.constructWithMagnitude(normalAngle - intersectionAngle, 2500)));
    PreciseLine preciseLine = new PreciseLine(newLine);
    preciseLine.setPreciseAngle(normalAngle - intersectionAngle);

    //    Ray is going through the mirror
    //    Something is wrong, abort
    //    if (Intersection.hasExitPoint(Shape.intersect(preciseLine, this), iPoint)) {
    //      return null;
    //    }

    double angle = Math.toDegrees(intersectionAngle) % 360;
    if (angle > 180) angle = 360 - angle;
    else if (angle < -180) angle += 360;
    AngleDisplay angleDisplay = new AngleDisplay("Incidence", String.format("%.1f", -angle), "Reflection", String
            .format("%.1f", angle));
    return new TransformData(preciseLine, angleDisplay, iData);
  }

  @Override
  public IntersectionSideData getIntersectionSideData(Point2D iPoint, Point2D origin, Ray r) {
    return Intersection.getIntersectionSide(iPoint, this, origin, false);
  }

  @Override
  public Line drawNormal(IntersectionSideData iData, Point2D iPoint) {
    double normalLength = 50;
    Line l = Geometry.createLineFromPoints(iPoint, iPoint.add(iData.normalVector.multiply(-normalLength / 2)));
    l.getStrokeDashArray().addAll(4d);
    return l;
  }


  @Override
  public byte[] serialize() {
    //    x,y,width,height,rotation
    ByteBuffer byteBuffer = ByteBuffer.allocate(Character.BYTES + Double.BYTES * 5);
    byteBuffer.putChar('m');
    byteBuffer.putDouble(this.getX());
    byteBuffer.putDouble(this.getY());
    byteBuffer.putDouble(this.getWidth());
    byteBuffer.putDouble(this.getHeight());
    byteBuffer.putDouble(this.getRotate());
    return byteBuffer.array();
  }

  @Override
  public OpticalRectangle clone(boolean shiftPositions) {
    return new Mirror(this.getX() + (shiftPositions ? 10 : 0), this.getY() + (shiftPositions ? 10 : 0), this
            .getWidth(), this.getHeight(), parent, this.getRotate());
  }
}
