package q1;

import java.awt.geom.Point2D;
public class CentredCircle extends Circle {
  private Point2D.Double centre;
  public CentredCircle() {
    this("yellow",10.0,new Point2D.Double(0,0));
  }
  public CentredCircle(String color, double radius, Point2D.Double centre) {
    super(color, radius);
    this.centre = centre;
  }
  public Point2D.Double getCentre() {
    return centre;
  }
  public void setCentre(Point2D.Double centre) {
    this.centre = centre;
  }
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Circle)) return false;
    CentredCircle c = (CentredCircle) obj;
    return super.equals(c) && c.getCentre().equals(this.centre);
  }
  @Override
  public String toString() {
    return "["+super.getColour()+", "+super.getRadius()+", ("+this.centre.getX()+", "+this.centre.getY()+")]";
  }
}