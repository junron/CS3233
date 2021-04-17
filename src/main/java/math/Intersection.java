package math;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.*;
import optics.PreciseJavaFXLine;
import optics.light.Ray;
import utils.Geometry;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Intersection {

  public static Point2D getIntersectionPoint(Path intersection, Vectors origin, boolean nearest) {
    ArrayList<Point2D> iPoints = convertToPoints(intersection.getElements());
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

  public static double getObjectIntersectionAngle(IntersectionSideData iData, PreciseJavaFXLine line) {
    //    Make angle in range [-180,180]
    double lineAngle = line.getPreciseAngle() - Math.PI;
    double normalAngle = iData.normalAngle;
    double angle = lineAngle - normalAngle;
    if (angle > Math.PI) {
      angle = angle - Math.PI * 2;
    }
    return angle;
  }

  public static IntersectionSideData getIntersectionSide(Ray r, Point2D iPoint, Rectangle intersector, Point2D origin
          , boolean isInObject) {
    return getIntersectionSide(r, iPoint, intersector, origin, isInObject, new ArrayList<>());
  }

  public static IntersectionSideData getIntersectionSide(Ray r, Point2D iPoint, Rectangle intersector, Point2D origin
          , boolean isInObject, ArrayList<Double> avoidNormalAngles) {
    Circle pointIndicator = new Circle(iPoint.getX(), iPoint.getY(), 1);
    ArrayList<Point2D> points = convertToPoints(((Path) Shape.intersect(intersector, intersector)).getElements());
    ArrayList<Line> lines = generateLineFromPoints(points);
    Stream<Line> stream = lines.stream().filter(line -> hasIntersectionPoint(Shape.intersect(line, pointIndicator)))
            .filter(line -> {
              Vectors v = Vectors.lineToVector(line);
              return !(avoidNormalAngles.contains(v.getAngle() - Math.PI / 2 - (isInObject ? Math.PI : 0)));
            });
    Line l = stream.min((Line a, Line b) -> {
      Point2D midA = Vectors.midPoint(a);
      Point2D midB = Vectors.midPoint(b);
      return (int) ((Vectors.distanceSquared(midA, origin) - Vectors.distanceSquared(midB, origin)) * 1000);
    }).orElse(null);

    if (l == null) return null;
    Vectors v = Vectors.lineToVector(l);
    return new IntersectionSideData(v, new Point2D(l.getStartX(), l.getStartY()), null, Vectors
            .constructWithMagnitude(v.getAngle() - Math.PI / 2 - (isInObject ? Math.PI : 0), 2));
  }

  private static double calculateAngleFromNormal(Ray r, Line intersectionLine, boolean isInObject) {
    double intersectionAngle = Math.PI * 2 - r.getCurrentJavaFXLine().getPreciseAngle();
    Vectors v = Vectors.lineToVector(intersectionLine);
    return Math.PI - intersectionAngle - (v.getAngle() - Math.PI / 2 - (isInObject ? Math.PI : 0));
  }

  private static ArrayList<Line> generateLineFromPoints(ArrayList<Point2D> points) {
    ArrayList<Line> res = new ArrayList<>();
    for (int i = 0; i < points.size(); i++) {
      Point2D p1 = points.get(i);
      Point2D p2 = i == points.size() - 1 ? points.get(0) : points.get(i + 1);
      res.add(Geometry.createLineFromPoints(p1, p2));
    }
    return res;
  }

  public static ArrayList<Point2D> convertToPoints(ObservableList<PathElement> elements) {
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
