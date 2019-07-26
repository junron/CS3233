package optics.objects;

import serialize.Serializable;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import math.IntersectionSideData;

public interface Interactable extends Serializable {
  Line transform(Line l, Point2D iPoint);
  IntersectionSideData getIntersectionSideData(Point2D iPoint);
  Line drawNormal(IntersectionSideData iData, Point2D iPoint);
}
