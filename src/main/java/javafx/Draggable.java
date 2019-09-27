package javafx;

import application.Storage;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import math.Intersection;

import java.util.ArrayList;

public class Draggable {
  private Point2D movementDelta;
  private Shape shape;
  private EventHandler<Event> onDrag;
  private EventHandler<Event> onDestroy;
  private Pane parent;

  public Draggable(Shape s, EventHandler<Event> onDrag, EventHandler<Event> onDestroy, Pane parent) {
    this.shape = s;
    this.onDrag = onDrag;
    this.onDestroy = onDestroy;
    this.parent = parent;
    if (this.shape instanceof Rectangle) {
      this.shape.setOnMousePressed(event -> movementDelta = new Point2D(((Rectangle) this.shape).getX() - event
              .getSceneX(), ((Rectangle) this.shape).getY() - event.getSceneY()));
    } else if (this.shape instanceof Circle) {
      this.shape.setOnMousePressed(event -> movementDelta = new Point2D(((Circle) this.shape).getCenterX() - event
              .getSceneX(), ((Circle) this.shape).getCenterY() - event.getSceneY()));
    }

    shape.setOnMouseDragged(event -> {
      // Prevent changes when animating
      if (Storage.isAnimating) return;
      // Prevent object from entering UI area
      double prevY = 0;
      if (this.shape instanceof Rectangle) {
        prevY = ((Rectangle) this.shape).getY();
        ((Rectangle) this.shape).setX(event.getSceneX() + movementDelta.getX());
        ((Rectangle) this.shape).setY(event.getSceneY() + movementDelta.getY());
      } else if (this.shape instanceof Circle) {
        prevY = ((Circle) this.shape).getCenterY();
        ((Circle) this.shape).setCenterX(event.getSceneX() + movementDelta.getX());
        ((Circle) this.shape).setCenterY(event.getSceneY() + movementDelta.getY());
      }
      if (isInUIArea(event) &&
              //        Except when moving object to trash
              !(event.getSceneX() > (parent.getWidth() - 82))) {
        if (this.shape instanceof Rectangle) ((Rectangle) this.shape).setY(prevY);
        else if (this.shape instanceof Circle) ((Circle) this.shape).setCenterY(prevY);
      }
      if (this.onDrag == null) return;
      this.onDrag.handle(event);
    });
    shape.setOnMouseReleased(e -> {
      if (e.getSceneY() > (parent.getHeight() - 165) && e.getSceneX() > (parent.getWidth() - 82)) {
        parent.getChildren().remove(this.shape);
        this.onDestroy.handle(e);
      }
    });
  }

  private boolean isInUIArea(MouseEvent event) {
    if (shape instanceof Circle) {
      return event.getSceneY() >= (parent.getHeight() - 165);
    } else if (shape instanceof Rectangle) {
      if (((Rectangle) shape).getY() + ((Rectangle) shape).getWidth() + ((Rectangle) shape).getHeight() < parent
              .getHeight() - 165) {
        return false;
      }
    }
    ObservableList<PathElement> intersection = ((Path) Shape.intersect(shape, shape)).getElements();
    ArrayList<Point2D> points = Intersection.convertToPoints(intersection);
    return (points.stream().anyMatch(point2D -> point2D.getY() >= (parent.getHeight() - 165)));
  }
}
