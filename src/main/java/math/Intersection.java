package math;

import javafx.geometry.Point2D;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Comparator;

public class Intersection {
  public static void intersections(Shape intersection) {

  }

  public static Point2D getIntersectionPoint(Path intersection, Vectors origin) {
    ArrayList<PathElement> elements = new ArrayList<>(intersection.getElements());
    //Inline extension of comparator iface
    elements.sort(new Comparator<>() {
      @Override
      public int compare(PathElement o1, PathElement o2) {
        return (int) (getDistance(o1) - getDistance(o2));
      }

      private double getDistance(PathElement element) {
        if (element instanceof MoveTo) {
          return Vectors.distanceBetween(new Vectors(((MoveTo) element).getX(), ((MoveTo) element).getY()),
                  origin);
        } else if (element instanceof LineTo) {
          return Vectors.distanceBetween(new Vectors(((LineTo) element).getX(), ((LineTo) element).getY()),
                  origin);
        } else {
          return Double.MAX_VALUE;
        }
      }
    });
    PathElement element = elements.get(0);
    if (element instanceof MoveTo) {
      return new Point2D(((MoveTo) element).getX(),((MoveTo) element).getY());
    } else {
      return new Point2D(((LineTo) element).getX(),((LineTo) element).getY());
    }
  }

  public static double getIntersectingAngle(Point2D iPoint, Rectangle intersector){
    System.out.println(intersector.getRotate());
    System.out.println(iPoint);
    System.out.println(intersector.getBoundsInParent());
    return 0;
  }

// Output:
//       0
//   +------+
//   |      |
//   |      |
//  3|      |1
//   |      |
//   |      |
//   +------+
//       2
  public static double getIntersectionSide(Point2D iPoint, Rectangle intersector){

    return 0;
  }

  private static boolean pointIsOnLine(Point2D point,double lineX,double lineY){
    double gradient = lineY/lineX;
    double intercept = lineY - (gradient*lineX);
    return point.getY() == ((gradient * point.getX()) + intercept);
  }

  public static boolean hasIntersectionPoint(Path intersection) {
    return intersection.getElements().size() > 0;
  }
}
