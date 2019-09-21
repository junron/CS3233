package optics.objects;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import serialize.Serializable;

import java.nio.ByteBuffer;

abstract public class OpticalRectangle extends Rectangle implements Interactive, Serializable {
  private final int maxSize = 10_00;
  private final int minSize = 5;
  protected Pane parent;

  public OpticalRectangle(double x, double y, double width, double height) {
    super(x, y, width, height);
  }

  public int setWidthChecked(double width) {
    if (width > maxSize) {
      this.setWidth(maxSize);
      return maxSize;
    }
    if (width < minSize) {
      this.setWidth(minSize);
      return minSize;
    }
    this.setWidth(width);
    return (int) width;
  }

  public int setHeightChecked(double height) {
    if (height > maxSize) {
      this.setHeight(maxSize);
      return maxSize;
    }
    if (height < minSize) {
      this.setHeight(minSize);
      return minSize;
    }
    this.setHeight(height);
    return (int) height;
  }

  public abstract OpticalRectangle clone(boolean shiftPositions);

  public Pane getRealParent() {
    return parent;
  }

  @Override
  public void deserialize(byte[] serialized) {
    ByteBuffer buffer = ByteBuffer.wrap(serialized);
    buffer.getChar();
    double x = buffer.getDouble();
    double y = buffer.getDouble();
    double width = buffer.getDouble();
    double height = buffer.getDouble();
    double angle = buffer.getDouble();
    this.setX(x);
    this.setY(y);
    this.setWidth(width);
    this.setHeight(height);
    this.setRotate(angle);
  }
}
