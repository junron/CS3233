package optics.objects;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import serialize.Serializable;

public abstract class OpticalRectangle extends Rectangle implements Interactive, Serializable {
  protected Pane parent;

  public OpticalRectangle(double x, double y, double width, double height) {
    super(x, y, width, height);
  }
  public abstract OpticalRectangle clone(boolean shiftPositions);

  public Pane getRealParent() {
    return parent;
  }
}
