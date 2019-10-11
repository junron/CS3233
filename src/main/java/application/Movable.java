package application;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;

class Movable {
  private static Movable currentObject;
  private static Pane parent;
  private static ArrayList<Movable> objects = new ArrayList<>();
  private final Shape shape;

  static void init(Pane parent) {
    Movable.parent = parent;
    Movable.parent.setOnMouseMoved(e -> {
      if (currentObject == null) return;
      reposition(e);
    });
  }

  Movable(Shape shape) {
    this.shape = shape;
    shape.setOnMouseClicked(e -> {
      if (currentObject == this) {
        currentObject = null;
        return;
      }
      currentObject = this;
    });
    objects.add(this);
  }

  Point2D getCoords() {
    if (shape instanceof Circle) {
      return new Point2D(((Circle) shape).getCenterX(), ((Circle) shape).getCenterY());
    } else if (shape instanceof Rectangle) {
      return new Point2D(((Rectangle) shape).getX(), ((Rectangle) shape).getY());
    }
    return new Point2D(0,0);
  }

  void setCoords(Point2D coords) {
    if (shape instanceof Circle) {
      ((Circle) shape).setCenterY(coords.getY());
      ((Circle) shape).setCenterX(coords.getX());
    } else if (shape instanceof Rectangle) {
      ((Rectangle) shape).setY(coords.getY());
      ((Rectangle) shape).setX(coords.getX());
    }
  }


  private static void reposition(MouseEvent evt) {
    Point2D prevCoords = currentObject.getCoords();
    currentObject.setCoords(new Point2D(evt.getSceneX(), evt.getSceneY()));
    for (Movable object : objects) {
      if (object == currentObject) continue;
      Shape intersect = Shape.intersect(object.shape, currentObject.shape);
      if (intersect instanceof Path && ((Path) intersect).getElements().size() > 0) {
        // Object intersects with another movable object
        currentObject.setCoords(prevCoords);
      }
    }
  }
}
