package optics;

import javafx.scene.shape.Line;

public class PreciseLine extends Line {
  //  Radians
  private double preciseAngle;

  public PreciseLine(double startX, double startY, double endX, double endY) {
    super(startX, startY, endX, endY);
  }
  public PreciseLine(Line l){
    this(l.getStartX(),l.getStartY(),l.getEndX(),l.getEndY());
  }

  public double getPreciseAngle() {
    return preciseAngle;
  }

  public void setPreciseAngle(double preciseAngle) {
    this.preciseAngle = preciseAngle;
  }
}
