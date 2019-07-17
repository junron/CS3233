package math;

import javafx.geometry.Point2D;

public class Vectors extends Point2D {
  public Vectors(double x, double y) {
    super(x, y);
  }

  public static Vectors constructWithMagnitude(double direction, double magnitude){
    return new Vectors(Math.cos(direction)*magnitude,Math.sin(direction)*magnitude);
  }

  public static double distanceBetween(Vectors a, Vectors b){
    Point2D c = a.subtract(b);
    return c.magnitude();
  }

  public double getAngle(){
    return Math.atan2(this.getY(),this.getX());
  }
}
