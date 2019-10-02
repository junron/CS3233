package optics.objects;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import serialize.Serializable;

abstract public class OpticalRectangle extends Rectangle implements Interactive, Serializable {
  private final int maxSize = 10_00;
  private final int minSize = 5;
  protected Pane parent;

  public OpticalRectangle(double x, double y, double width, double height) {
    super(x, y, width, height);
  }

  public void setWidthChecked(double width) {
    if (width > maxSize) {
      this.setWidth(maxSize);
      return;
    }
    if (width < minSize) {
      this.setWidth(minSize);
      return;
    }
    this.setWidth(width);
  }

  public void setHeightChecked(double height) {
    if (height > maxSize) {
      this.setHeight(maxSize);
      return;
    }
    if (height < minSize) {
      this.setHeight(minSize);
      return;
    }
    this.setHeight(height);
  }

  public abstract OpticalRectangle clone(boolean shiftPositions);

  public Pane getRealParent() {
    return parent;
  }

  public String serialize(char id) {
    return id + "|" + this.getX() + "|" + this.getY() + "|" + this.getWidth() + "|" + this.getHeight() + "|" + this
            .getRotate();
  }

  @Override
  public void deserialize(String string) {
    String[] parts = string.split("\\|");
    this.setX(Double.parseDouble(parts[1]));
    this.setY(Double.parseDouble(parts[2]));
    this.setWidth(Double.parseDouble(parts[3]));
    this.setHeight(Double.parseDouble(parts[4]));
    this.setRotate(Double.parseDouble(parts[5]));
  }

  public void clone(OpticalRectangle opticalRectangle) {
    opticalRectangle.setX(this.getX());
    opticalRectangle.setY(this.getY());
    opticalRectangle.setRotate(this.getRotate());
    opticalRectangle.setHeight(this.getHeight());
    opticalRectangle.setWidth(this.getWidth());
  }
}
