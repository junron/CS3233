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
  public static Circle createCircleFromPoint(Point2D p1,double radius){
    return new Circle(p1.getX(),p1.getY(),radius);
  }
  public static OpticalRectangle getNearestIntersection(Line l, OpticsList<OpticalRectangle> interactiveObjects,OpticalRectangle currentObj){
    Vectors origin = new Vectors(l.getStartX(),l.getStartY());
    OpticalRectangle result = null;
    double bestIntersectionDistance = Double.MAX_VALUE;
    for(OpticalRectangle i: interactiveObjects){
      Path intersection = (Path) Shape.intersect(l, i);
      if(Intersection.hasIntersectionPoint(intersection)){
        Point2D iPoint = Intersection.getIntersectionPoint(intersection,origin,!(i instanceof Refract) && i!=currentObj);
        if(iPoint.equals(origin)) continue;
        double distance = Vectors.distanceBetween(iPoint,origin);
        if(distance<bestIntersectionDistance){
          bestIntersectionDistance = distance;
          result = i;
        }
      }
    }
    return result;
  }
  public static OpticalRectangle getNearestIntersection(Line l, OpticsList<OpticalRectangle> interactiveObjects){
    return getNearestIntersection(l,interactiveObjects,null);
  }
  public static String fixAngle(double angle){
    angle %=360;
    if(angle<0) angle+=360;
    return String.valueOf(angle);
  }
}
