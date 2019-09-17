package optics;

import javafx.scene.shape.Line;

public class PreciseLine extends Line {
  //  Radians
  private double preciseAngle;

  public PreciseLine(double startX, double startY, double endX, double endY) {
    super(startX, startY, endX, endY);
  }

  public PreciseLine(Line l) {
    this(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY());
  }

  public double getPreciseAngle() {
    // System.out.println(Math.toDegrees(preciseAngle) + " " + Math.toDegrees(calculateAngle()));
    return preciseAngle;
  }

  // private double calculateAngle() {
  //   System.out.println(this.getStartY() + " " + this.getEndY());
  //   return Math.atan2(this.getEndY() - this.getStartY(), this.getEndX() - this.getStartX());
  // }

  public void setPreciseAngle(double preciseAngle) {
    this.preciseAngle = preciseAngle;
  }
}
