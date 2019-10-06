package optics.objects;

import application.Storage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import serialize.Serializable;

abstract public class OpticalRectangle extends Rectangle implements Interactive, Serializable {
  private final int maxSize = 10_00;
  private final int minSize = 5;
  private double realX;
  private double realY;
  protected Pane parent;

  public OpticalRectangle(double x, double y, double width, double height) {
    super(x, y, width, height);
    this.realX = x - Storage.getOffset().getX();
    this.realY = y - Storage.getOffset().getY();
    this.setViewOrder(100);
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
    return id + "|" + this.realX + "|" + this.realY + "|" + this.getWidth() + "|" + this.getHeight() + "|" + this
            .getRotate();
  }

  @Override
  public void deserialize(String string) {
    String[] parts = string.split("\\|");
    this.realX = Double.parseDouble(parts[1]);
    this.realY = Double.parseDouble(parts[2]);
    this.reposition();
    this.setWidth(Double.parseDouble(parts[3]));
    this.setHeight(Double.parseDouble(parts[4]));
    this.setRotate(Double.parseDouble(parts[5]));
  }

  public void clone(OpticalRectangle opticalRectangle) {
    opticalRectangle.setX(this.getX());
    opticalRectangle.setY(this.getY());
    opticalRectangle.realY = this.realY;
    opticalRectangle.realX = this.realX;
    opticalRectangle.setRotate(this.getRotate());
    opticalRectangle.setHeight(this.getHeight());
    opticalRectangle.setWidth(this.getWidth());
  }

  public void setScreenX(double x) {
    this.setX(x);
    this.realX = x - Storage.getOffset().getX();
  }

  public void setScreenY(double y) {
    this.setY(y);
    this.realY = y - Storage.getOffset().getY();
  }

  public void reposition() {
    this.setX(this.realX + Storage.getOffset().getX());
    this.setY(this.realY + Storage.getOffset().getY());
  }
}
