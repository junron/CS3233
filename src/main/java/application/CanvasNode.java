package application;

import javafx.geometry.Point2D;

public interface CanvasNode {
  void reposition(Point2D prevOffset, Point2D offset);
}
