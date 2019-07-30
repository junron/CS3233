package math;

import enums.Sides;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.*;
import utils.Geometry;

import java.util.ArrayList;

public class Intersection {

  public static Point2D getIntersectionPoint(Path intersection, Vectors origin,boolean nearest) {
    ArrayList<Point2D> iPoints = convertToPoints(intersection.getElements());
    //Inline extension of comparator iface
    if(nearest){
      iPoints.sort((o1, o2) ->
              (int) (Vectors.distanceBetween(o1, origin) - Vectors.distanceBetween(o2, origin)));
    }else{
      iPoints.sort((o1, o2) ->
              (int) (Vectors.distanceBetween(o2, origin) - Vectors.distanceBetween(o1, origin)));
    }
    return iPoints.get(0);
  }
  public static Point2D getIntersectionPoint(Path intersection, Vectors origin){
    return getIntersectionPoint(intersection,origin,true);
  }

  public static double getIntersectingAngle(IntersectionSideData iData, Line line) {
    Vectors vLine = new Vectors(Vectors.lineToVector(line).multiply(-1));
    double lineAngle = vLine.getAngle();
    double normalAngle = iData.normalVector.getAngle();
    return lineAngle-normalAngle;
  }

  //  This is pure genius
  public static IntersectionSideData getIntersectionSide(Point2D iPoint, Rectangle intersector) {
    Circle pointIndicator = new Circle(iPoint.getX(), iPoint.getY(), 1);
    ArrayList<Point2D> points = convertToPoints(((Path) Shape.intersect(intersector, intersector)).getElements());
    ArrayList<Line> lines = generateLinesPoints(points);
    Line l = lines.stream()
                  .filter(line -> hasIntersectionPoint(Shape.intersect(line, pointIndicator)))
                  .findFirst()
                  .orElse(null);
    Vectors v = Vectors.lineToVector(l);
    IntersectionSideData result = new IntersectionSideData(
            v,
            new Point2D(l.getStartX(), l.getStartY()),
            null,
            null
    );
    Point2D sameAngle = Vectors.constructWithMagnitude(Math.toRadians(intersector.getRotate()), 2);
    Point2D perpendicular = Vectors.constructWithMagnitude(Math.toRadians(intersector.getRotate() - 90), 2);

    if (!hasIntersectionPoint(iPoint.add(sameAngle), intersector)) {
      result.normalVector = new Vectors(sameAngle);
      result.side = Sides.RIGHT;
    } else if (!hasIntersectionPoint(iPoint.subtract(sameAngle), intersector)) {
      result.normalVector = new Vectors(sameAngle.multiply(-1));
      result.side = Sides.LEFT;
    } else if (!hasIntersectionPoint(iPoint.subtract(perpendicular), intersector)) {
      result.normalVector = new Vectors(perpendicular.multiply(-1));
      result.side = Sides.BOTTOM;
    } else {
      result.normalVector = new Vectors(perpendicular);
      result.side = Sides.TOP;
    }
    return result;
  }


  private static ArrayList<Line> generateLinesPoints(ArrayList<Point2D> points) {
    ArrayList<Line> res = new ArrayList<>();
    for (int i = 0; i < points.size(); i++) {
      Point2D p1 = points.get(i);
      Point2D p2 = i == points.size() - 1 ? points.get(0) : points.get(i + 1);
      res.add(Geometry.createLineFromPoints(p1, p2));
    }
    return res;
  }

  private static ArrayList<Point2D> convertToPoints(ObservableList<PathElement> elements) {
    ArrayList<Point2D> results = new ArrayList<>();
    for (PathElement elem : elements) {
      if (elem instanceof MoveTo) {
        results.add(new Point2D(((MoveTo) elem).getX(), ((MoveTo) elem).getY()));
      } else if (elem instanceof LineTo) {
        results.add(new Point2D(((LineTo) elem).getX(), ((LineTo) elem).getY()));
      }
    }
    return results;
  }

  public static boolean hasIntersectionPoint(Path intersection) {
    return intersection.getElements().size() > 0;
  }

  public static boolean hasIntersectionPoint(Shape intersection) {
    return hasIntersectionPoint((Path) intersection);
  }

  public static boolean hasIntersectionPoint(Point2D point, Shape shape) {
    return hasIntersectionPoint(Shape.intersect(Geometry.createCircleFromPoint(point, 1), shape));
  }
}
