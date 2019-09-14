package math;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.*;
import optics.PreciseLine;
import utils.Geometry;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Intersection {

  public static Point2D getIntersectionPoint(Path intersection, Vectors origin, boolean nearest) {
    ArrayList<Point2D> iPoints = convertToPoints(intersection.getElements());
    //Inline extension of comparator iface
    if (nearest) {
      iPoints.sort((o1, o2) -> (int) (Vectors.distanceBetween(o1, origin) - Vectors.distanceBetween(o2, origin)));
    } else {
      iPoints.sort((o1, o2) -> (int) (Vectors.distanceBetween(o2, origin) - Vectors.distanceBetween(o1, origin)));
    }
    return iPoints.get(0);
  }

  public static Point2D getIntersectionPoint(Path intersection, Vectors origin) {
    return getIntersectionPoint(intersection, origin, true);
  }

  public static double getObjectIntersectionAngle(IntersectionSideData iData, PreciseLine line) {
    //    Make angle in range [-180,180]
    double lineAngle = line.getPreciseAngle() - Math.PI;
    double normalAngle = iData.normalAngle;
    double angle = lineAngle - normalAngle;
    if (angle > Math.PI) {
      angle = angle - Math.PI * 2;
    }
    return angle;
  }

  public static IntersectionSideData getIntersectionSide(Point2D iPoint, Rectangle intersector, Point2D origin) {
    Circle pointIndicator = new Circle(iPoint.getX(), iPoint.getY(), 1);
    ArrayList<Point2D> points = convertToPoints(((Path) Shape.intersect(intersector, intersector)).getElements());
    ArrayList<Line> lines = generateLinesPoints(points);
    Stream<Line> stream = lines.stream().filter(line -> hasIntersectionPoint(Shape.intersect(line, pointIndicator)));
    Line l = stream.min((Line a, Line b) -> {
      Point2D midA = Vectors.midPoint(a);
      Point2D midB = Vectors.midPoint(b);
      return (int) (Vectors.distanceSquared(midA, origin) - Vectors.distanceSquared(midB, origin));
    }).orElse(null);

    if (l == null) return null;
    Vectors v = Vectors.lineToVector(l);
    return new IntersectionSideData(v, new Point2D(l.getStartX(), l.getStartY()), null, Vectors
            .constructWithMagnitude(v.getAngle() - Math.toRadians(90), 2));
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

  public static boolean hasExitPoint(Path intersection, Point2D entryPoint) {
    ObservableList<PathElement> elements = intersection.getElements();
    if (elements.size() == 0) return false;
    ArrayList<Point2D> points = convertToPoints(elements);
    //    Remove close points
    points.removeIf(point2D -> Vectors.distanceSquared(entryPoint, point2D) < 100);
    return points.size() > 0;
  }

  public static boolean hasIntersectionPoint(Shape intersection) {
    return hasIntersectionPoint((Path) intersection);
  }

  public static boolean hasExitPoint(Shape intersection, Point2D entryPoint) {
    return hasExitPoint((Path) intersection, entryPoint);
  }

  public static boolean hasIntersectionPoint(Point2D point, Shape shape) {
    return hasIntersectionPoint(Shape.intersect(Geometry.createCircleFromPoint(point, 1), shape));
  }
}
