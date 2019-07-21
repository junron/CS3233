package optics;

import javafx.scene.shape.Rectangle;

public abstract class OpticalRectangle extends Rectangle implements Interactable {
  public OpticalRectangle(double x, double y, double width, double height) {
    super(x, y, width, height);
  }
}
