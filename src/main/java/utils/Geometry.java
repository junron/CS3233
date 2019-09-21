package utils;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import math.Intersection;
import math.Vectors;
import optics.objects.OpticalRectangle;
import optics.objects.Refract;

public class Geometry {
  public static Line createLineFromPoints(Point2D p1, Point2D p2) {
    return new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  public static Circle createCircleFromPoint(Point2D p1, double radius) {
    return new Circle(p1.getX(), p1.getY(), radius);
  }

  public static OpticalRectangle getNearestIntersection(Line l, OpticsList<OpticalRectangle> interactiveObjects,
                                                        OpticalRectangle currentObj) {
    //    Intersection point must be at least 1 px away from origin
    boolean hasObj = currentObj != null;
    double threshold = 4;
    Vectors origin = new Vectors(l.getStartX(), l.getStartY());
    OpticalRectangle result = null;
    double bestIntersectionDistance = Double.MAX_VALUE;
    for (OpticalRectangle i : interactiveObjects) {
      boolean isCurrObj = hasObj && i.equals(currentObj);
      if (isCurrObj && !(i instanceof Refract)) continue;
      Path intersection = (Path) Shape.intersect(l, i);
      if (Intersection.hasIntersectionPoint(intersection)) {
        Point2D iPoint = Intersection.getIntersectionPoint(intersection, origin, !isCurrObj);
        double distance = Vectors.distanceSquared(iPoint, origin);
        if (distance < threshold) {
          continue;
        }
        if (distance < bestIntersectionDistance) {
          bestIntersectionDistance = distance;
          result = i;
        }
      }
    }
    return result;
  }

  public static OpticalRectangle getNearestIntersection(Line l, OpticsList<OpticalRectangle> interactiveObjects) {
    return getNearestIntersection(l, interactiveObjects, null);
  }

  public static String fixAngle(double angle, int decimalPlaces) {
    angle %= 360;
    if (angle < 0) angle += 360;
    return String.format("%." + decimalPlaces + "f", angle);
  }

  public static String fixAngle(double angle) {
    return fixAngle(angle, 1);
  }

  public static double fixAngleRadians(double angle) {
    return fixAngleRadians(angle, Math.PI * 2);
  }

  public static double fixAngleRadians(double angle, double mod) {
    angle %= mod;
    if (angle < 0) angle += mod;
    return angle;
  }
}
