package javafx;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class Draggable {
  private Point2D movementDelta;
  private Rectangle shape;
  private EventHandler<Event> onDrag;
  private EventHandler<Event> onDestroy;
  private Pane parent;

  public Draggable(Rectangle s, EventHandler<Event> onDrag, EventHandler<Event> onDestroy, Pane parent) {
    this.shape = s;
    this.onDrag = onDrag;
    this.onDestroy = onDestroy;
    this.parent = parent;
    this.shape.setOnMousePressed(event -> movementDelta = new Point2D(shape.getX() - event.getSceneX(), shape
            .getY() - event.getSceneY()));
    shape.setOnMouseDragged(event -> {
      shape.setX(event.getSceneX() + movementDelta.getX());
      shape.setY(event.getSceneY() + movementDelta.getY());
      if(this.onDrag ==null) return;
      this.onDrag.handle(event);
    });
    shape.setOnMouseReleased(e->{
      if(e.getSceneY()>(400-82) && e.getSceneX()>(600-82)){
        parent.getChildren().remove(this.shape);
        this.onDestroy.handle(e);
      }
    });
  }
}
