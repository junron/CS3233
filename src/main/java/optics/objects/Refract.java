package optics.objects;

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
import optics.light.Ray;
import utils.Geometry;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Function;

public class Refract extends OpticalRectangle {
  private double refractiveIndex;
  private ArrayList<EventHandler<Event>> onStateChange = new ArrayList<>();
  private Function<Event, Void> onDestroy;

  public Refract(double x, double y, double width, double height, Pane parent, double rotation, double refractiveIndex) {
    super(x, y, width, height);
    this.refractiveIndex = refractiveIndex;
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
  public Line transform(Ray r, Point2D iPoint) {
    Line l = r.getCurrentLine();
    l.setEndX(iPoint.getX());
    l.setEndY(iPoint.getY());
    IntersectionSideData iData = getIntersectionSideData(iPoint);
    double normalAngle = iData.normalVector.getAngle();
    double intersectionAngle = Intersection.getIntersectingAngle(iData, l);
    double refAngle = Math.asin(Math.sin(intersectionAngle) / this.refractiveIndex);
    if (Double.isNaN(refAngle)) {
      r.setCurrentRefractiveIndex(this.refractiveIndex);
//      Total internal reflection
      return Geometry.createLineFromPoints(iPoint, iPoint
              .add(Vectors.constructWithMagnitude(normalAngle - intersectionAngle, 2500)));
    }
    if (this.refractiveIndex == r.getCurrentRefractiveIndex()) {
//      System.out.println(r.getCurrentRefractiveIndex());
      r.setCurrentRefractiveIndex(1);
      refAngle = Math.asin(this.refractiveIndex * Math.sin(intersectionAngle+Math.toRadians(180)));
//      System.out.println(Math.toDegrees(refAngle));
    } else {
      r.setCurrentRefractiveIndex(this.refractiveIndex);
    }
    Vectors vect = Vectors.constructWithMagnitude(refAngle, 2500);
    System.out.println("Res"+Math.toDegrees(vect.getAngle()));
    return Geometry.createLineFromPoints(iPoint, iPoint
            .add(vect));
  }

  @Override
  public IntersectionSideData getIntersectionSideData(Point2D iPoint) {
    return Intersection.getIntersectionSide(iPoint, this);
  }

  @Override
  public Line drawNormal(IntersectionSideData iData, Point2D iPoint) {
    double normalLength = 50;
    Line l = Geometry.createLineFromPoints(iPoint, iPoint.add(iData.normalVector.multiply(normalLength / 2)));
    l.getStrokeDashArray().addAll(4d);
    return l;
  }


  @Override
  public byte[] serialize() {
//    x,y,width,height,rotation,ref index
    ByteBuffer byteBuffer = ByteBuffer.allocate(Character.BYTES + Double.BYTES * 6);
    byteBuffer.putChar('e');
    byteBuffer.putDouble(this.getX());
    byteBuffer.putDouble(this.getY());
    byteBuffer.putDouble(this.getWidth());
    byteBuffer.putDouble(this.getHeight());
    byteBuffer.putDouble(this.getRotate());
    byteBuffer.putDouble(this.refractiveIndex);
    return byteBuffer.array();
  }

  @Override
  public void deserialize(byte[] serialized) {
    ByteBuffer buffer = ByteBuffer.wrap(serialized);
    buffer.getChar();
    double x = buffer.getDouble();
    double y = buffer.getDouble();
    double width = buffer.getDouble();
    double height = buffer.getDouble();
    double angle = buffer.getDouble();
    double refractiveIndex = buffer.getDouble();
    this.setX(x);
    this.setY(y);
    this.setWidth(width);
    this.setHeight(height);
    this.setRotate(angle);
    this.refractiveIndex = refractiveIndex;
  }

  @Override
  public OpticalRectangle clone(boolean shiftPositions) {
    return new Refract(this.getX()+(shiftPositions?10:0),this.getY()+(shiftPositions?10:0),this.getWidth(),this.getHeight(),this.parent,this.getRotate(),this.refractiveIndex);
  }
}
