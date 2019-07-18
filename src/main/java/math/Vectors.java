package math;

import javafx.geometry.Point2D;

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
}
