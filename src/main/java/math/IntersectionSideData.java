package math;

import enums.Sides;
import javafx.geometry.Point2D;

public class IntersectionSideData {
  @Override
  public String toString() {
    return "IntersectionSideData{" + "lineVector=" + lineVector + ", startPoint=" + startPoint + ", side=" + side +
            ", normalVector=" + normalVector + '}';
  }

  public Vectors lineVector;
  public Point2D startPoint;
  public Sides side;
  public Vectors normalVector;
  public double normalAngle;

  public IntersectionSideData(Vectors lineVector, Point2D startPoint, Sides side, Vectors normalVector) {
    this.lineVector = lineVector;
    this.startPoint = startPoint;
    this.side = side;
    this.normalVector = normalVector;
    this.normalAngle = normalVector.getAngle() + Math.PI;
  }


}
