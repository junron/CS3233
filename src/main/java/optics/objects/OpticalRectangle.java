package optics.objects;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import serialize.Serializable;

public abstract class OpticalRectangle extends Rectangle implements Interactive, Serializable {
  protected Pane parent;

  private final int maxSize = 10_00;
  private final int minSize = 5;
  public int setWidthChecked(double width){
    if(width>maxSize){
      this.setWidth(maxSize);
      return maxSize;
    }
    if(width<minSize){
      this.setWidth(minSize);
      return minSize;
    }
    this.setWidth(width);
    return (int)width;
  }
  public int setHeightChecked(double height){
    if(height>maxSize){
      this.setHeight(maxSize);
      return maxSize;
    }
    if(height<minSize){
      this.setHeight(minSize);
      return minSize;
    }
    this.setHeight(height);
    return (int)height;
  }
  public OpticalRectangle(double x, double y, double width, double height) {
    super(x, y, width, height);
  }
  public abstract OpticalRectangle clone(boolean shiftPositions);

  public Pane getRealParent() {
    return parent;
  }
}
