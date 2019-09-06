package optics.objects;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import math.IntersectionSideData;
import optics.TransformData;
import optics.light.Ray;
import serialize.Serializable;

import java.util.function.Function;

public interface Interactive extends Serializable {
  TransformData transform(Ray r, Point2D iPoint);
  IntersectionSideData getIntersectionSideData(Point2D iPoint,Point2D origin);
  Line drawNormal(IntersectionSideData iData, Point2D iPoint);
  void addOnStateChange(EventHandler<Event> handler);
  void setOnDestroy(Function<Event, Void> onDestroy);
  Interactive cloneObject();
}
