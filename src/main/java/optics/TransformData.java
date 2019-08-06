package optics;

import javafx.AngleDisplay;
import math.IntersectionSideData;

public class TransformData {
  private PreciseLine preciseLine;
  private AngleDisplay angleDisplay;
  private IntersectionSideData intersectionSideData;

  public TransformData(PreciseLine preciseLine, AngleDisplay angleDisplay, IntersectionSideData intersectionSideData) {
    this.preciseLine = preciseLine;
    this.angleDisplay = angleDisplay;
    this.intersectionSideData = intersectionSideData;
  }

  public PreciseLine getPreciseLine() {
    return preciseLine;
  }

  public AngleDisplay getAngleDisplay() {
    return angleDisplay;
  }

  public IntersectionSideData getIntersectionSideData() {
    return intersectionSideData;
  }
}
