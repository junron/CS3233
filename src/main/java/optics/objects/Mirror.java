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
import utils.Geometry;

import java.util.ArrayList;
import java.util.function.Function;

public class Mirror extends OpticalRectangle {
  private ArrayList<EventHandler<Event>> onStateChange = new ArrayList<>();
  private Function<Event, Void> onDestroy;
  private Draggable draggable;
  private Rotatable rotatable;

  public Mirror(double x, double y, double width, double height, Pane parent, double rotation) {
    super(x, y, width, height);
    this.setRotate(rotation);
    this.setArcHeight(0);
    this.setArcWidth(0);
    this.setFill(Color.color(5 / 255.0, 213 / 255.0, 255 / 255.0, 0.28));
    this.setStrokeWidth(1);
    this.setStroke(Color.BLACK);
    this.draggable = new Draggable(this, this::triggerStateChange, this::triggerDestroy, parent);
    this.rotatable = new Rotatable(this, this::triggerStateChange);
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

  @Override
  public Line transform(Line l, Point2D iPoint) {
    l.setEndX(iPoint.getX());
    l.setEndY(iPoint.getY());
    IntersectionSideData iData = getIntersectionSideData(iPoint);
    double normalAngle = iData.normalVector.getAngle();
    double intersectionAngle = Intersection.getIntersectingAngle(iData, l);
    return Geometry.createLineFromPoints(iPoint, iPoint
            .add(Vectors.constructWithMagnitude(normalAngle - intersectionAngle, 1000)));
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

  public void setOnDestroy(Function<Event, Void> onDestroy) {
    this.onDestroy = onDestroy;
  }
}
