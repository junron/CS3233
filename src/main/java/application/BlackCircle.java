package application;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

class BlackCircle extends Circle implements CanvasNode {

  private final Movable movable;

  BlackCircle(double x, double y) {
    super(x, y, 10, Color.BLACK);
    this.setStroke(Color.RED);
    this.setStrokeWidth(1);
    // Get around the java multiple class extending issue
    movable = new Movable(this);
  }

  @Override
  public void reposition(Point2D prevOffset, Point2D offset) {
    System.out.println(movable.getCoords().subtract(prevOffset).add(offset));
    movable.setCoords(movable.getCoords().subtract(prevOffset).add(offset));
  }
}
