package optics.objects;

import javafx.scene.shape.Rectangle;
import serialize.Serializable;

public abstract class OpticalRectangle extends Rectangle implements Interactable, Serializable {
  public OpticalRectangle(double x, double y, double width, double height) {
    super(x, y, width, height);
  }
}
