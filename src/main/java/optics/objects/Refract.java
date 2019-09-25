package optics.objects;

import javafx.AngleDisplay;
import javafx.Draggable;
import javafx.KeyActions;
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

public class Refract extends OpticalRectangle {
  private double refractiveIndex;
  private ArrayList<EventHandler<Event>> onStateChange = new ArrayList<>();
  private Function<Event, Void> onDestroy;

  public Refract(double x, double y, double width, double height, Pane parent, double rotation,
                 double refractiveIndex) {
    super(x, y, width, height);
    this.refractiveIndex = refractiveIndex;
    this.setRotate(rotation);
    this.setArcHeight(0);
    this.setArcWidth(0);
    this.setFill(Color.color(5 / 255.0, 213 / 255.0, 255 / 255.0, 0.50));
    this.setStrokeWidth(1);
    this.setStroke(Color.BLACK);
    this.parent = parent;
    new Draggable(this, this::triggerStateChange, this::triggerDestroy, parent);
    new KeyActions(this, this::triggerStateChange);
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

  public double getRefractiveIndex() {
    return refractiveIndex;
  }

  public void setRefractiveIndex(double refractiveIndex) {
    this.refractiveIndex = Math.round(Math.max(1.0, refractiveIndex) * 100) / 100.0;
  }

  @Override
  public Interactive cloneObject() {
    return this.clone(false);
  }

  @Override
  public TransformData transform(Ray r, Point2D iPoint) {
    PreciseLine l = r.getCurrentLine();
    //    System.out.println(Math.toDegrees(l.getPreciseAngle()));
    l.setEndX(iPoint.getX());
    l.setEndY(iPoint.getY());
    IntersectionSideData iData = getIntersectionSideData(iPoint, new Point2D(l.getStartX(), l.getStartY()), r);
    double intersectionAngle = Math.PI * 2 - l.getPreciseAngle();
    double normalAngle = iData.normalAngle;
    double incidence = Math.PI - intersectionAngle - normalAngle;
    double refAngle = Math.asin(Math.sin(incidence) / this.refractiveIndex);
    if (r.isInRefractiveMaterial()) {
      r.setInRefractiveMaterial(false);
      refAngle = Math.asin(this.refractiveIndex * Math.sin(incidence));
      //      Total internal reflection can only occur when light exits an object
      if (Double.isNaN(refAngle)) {
        return totalInternalReflection(iPoint, r, iData);
      }
    } else {
      r.setInRefractiveMaterial(true);
    }
    Vectors vect = Vectors.constructWithMagnitude(refAngle + normalAngle - Math.PI, 2500);
    PreciseLine pLine = new PreciseLine(Geometry.createLineFromPoints(iPoint, iPoint.add(vect)));
    pLine.setPreciseAngle(refAngle + normalAngle - Math.PI);
    double iAngle = Math.toDegrees(incidence) % 360;
    if (iAngle > 180) iAngle = 360 - iAngle;
    else if (iAngle < -180) iAngle += 360;
    String angle = String.format("%.1f", Math.toDegrees(refAngle));
    String iAngleStr = String.format("%.1f", iAngle);
    AngleDisplay angleDisplay = new AngleDisplay("Incidence", iAngleStr, "Refraction", angle);
    return new TransformData(pLine, angleDisplay, iData);
  }

  //  Total internal reflection occurs when a ray travels from inside a high refractive index
  //  object to the air. The ray is internally reflected within the object
  private TransformData totalInternalReflection(Point2D iPoint, Ray r, IntersectionSideData iData) {
    double normalAngle = iData.normalAngle;
    double intersectionAngle = Intersection.getObjectIntersectionAngle(iData, r.getCurrentLine());
    PreciseLine pLine = new PreciseLine(Geometry.createLineFromPoints(iPoint, iPoint
            .add(Vectors.constructWithMagnitude(normalAngle - intersectionAngle, 2500))));
    pLine.setPreciseAngle(normalAngle - intersectionAngle);
    r.setInRefractiveMaterial(true);
    //    Angle display
    AngleDisplay angleDisplay = new AngleDisplay("TIR", String
            .format("%.1f", Math.toDegrees(normalAngle - intersectionAngle)));
    return new TransformData(pLine, angleDisplay, iData);
  }

  @Override
  public IntersectionSideData getIntersectionSideData(Point2D iPoint, Point2D origin, Ray r) {
    return Intersection.getIntersectionSide(r, iPoint, this, origin, r.isInRefractiveMaterial());
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
    //    x,y,width,height,rotation,ref index
    ByteBuffer byteBuffer = super.serialize('e', Character.BYTES + Double.BYTES * 6);
    byteBuffer.putDouble(this.refractiveIndex);
    return byteBuffer.array();
  }

  @Override
  public void deserialize(byte[] serialized) {
    super.deserialize(serialized);
    ByteBuffer buffer = ByteBuffer.wrap(serialized);
    this.refractiveIndex = buffer.getDouble(Character.BYTES + Double.BYTES * 5);
  }

  @Override
  public OpticalRectangle clone(boolean shiftPositions) {
    return new Refract(this.getX() + (shiftPositions ? 10 : 0), this.getY() + (shiftPositions ? 10 : 0), this
            .getWidth(), this.getHeight(), this.parent, this.getRotate(), this.refractiveIndex);
  }
}
