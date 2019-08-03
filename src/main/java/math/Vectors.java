package math;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;

public class Vectors extends Point2D {
  public Vectors(double x, double y) {
    super(x, y);
  }

  public Vectors(Point2D point){
    this(point.getX(),point.getY());
  }

  public static Vectors constructWithMagnitude(double direction, double magnitude){
    return new Vectors(Math.cos(direction)*magnitude,Math.sin(direction)*magnitude);
  }

  public static double distanceBetween(Point2D a, Point2D b){
    Point2D c = a.subtract(b);
    return c.magnitude();
  }

  public double getAngle(){
    return Math.atan2(this.getY(),this.getX());
  }

  public static Vectors lineToVector(Line l){
    Point2D start = new Point2D(l.getStartX(),l.getStartY());
    Point2D end = new Point2D(l.getEndX(),l.getEndY());
    return new Vectors(end.subtract(start));
  }
  public static double distanceSquared(Point2D a, Point2D b){
    return Math.pow(a.getX()-b.getX(),2)+Math.pow(a.getY()-b.getY(),2);
  }
}
