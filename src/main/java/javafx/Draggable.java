package javafx;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

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
              .getSceneX(),
              ((Rectangle) this.shape).getY() - event.getSceneY()));
    } else if (this.shape instanceof Circle) {
      this.shape.setOnMousePressed(event -> movementDelta = new Point2D(((Circle) this.shape).getCenterX() - event
              .getSceneX(),
              ((Circle) this.shape).getCenterY() - event.getSceneY()));
    }

    shape.setOnMouseDragged(event -> {
      if(this.shape instanceof Rectangle){
        ((Rectangle) this.shape).setX(event.getSceneX() + movementDelta.getX());
        ((Rectangle)this.shape).setY(event.getSceneY() + movementDelta.getY());
      }else if(this.shape instanceof Circle){
        ((Circle) this.shape).setCenterX(event.getSceneX() + movementDelta.getX());
        ((Circle) this.shape).setCenterY(event.getSceneY() + movementDelta.getY());
      }

      if (this.onDrag == null) return;
      this.onDrag.handle(event);
    });
    shape.setOnMouseReleased(e -> {
      if (e.getSceneY() > (parent.getHeight() - 82) && e.getSceneX() > (parent.getWidth() - 82)) {
        parent.getChildren().remove(this.shape);
        this.onDestroy.handle(e);
      }
    });
  }
}
