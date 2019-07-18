package math;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;

public class Intersection {
  public static void intersections(Shape intersection) {

  }

  public static Point2D getIntersectionPoint(Path intersection, Vectors origin) {
    ArrayList<Point2D> iPoints = convertToPoints(intersection.getElements());
    //Inline extension of comparator iface
    iPoints.sort((o1, o2) ->
            (int) (Vectors.distanceBetween(o1, origin) - Vectors.distanceBetween(o2, origin)));
    return iPoints.get(0);
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
  public static double getIntersectionSide(Point2D iPoint, Rectangle intersector, Pane parent){
    ArrayList<Point2D> iPoints = convertToPoints(((Path)Shape.intersect(intersector,intersector)).getElements());

    Line line1 = createLineFromPoints(iPoints.get(0),iPoints.get(1));
    line1.setStroke(Color.ORANGE);
    parent.getChildren().add(line1);
    return 0;
  }

  private static Line createLineFromPoints(Point2D p1, Point2D p2){
    return new Line(p1.getX(),p1.getY(),p2.getX(),p2.getY());
  }

  private static ArrayList<Point2D> convertToPoints(ObservableList<PathElement> elements){
    ArrayList<Point2D> results = new ArrayList<>();
    for(PathElement elem: elements){
      if (elem instanceof MoveTo) {
        results.add(new Point2D(((MoveTo) elem).getX(), ((MoveTo) elem).getY()));
      } else if (elem instanceof LineTo) {
        results.add(new Point2D(((LineTo) elem).getX(), ((LineTo) elem).getY()));
      }
    }
    return results;
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
