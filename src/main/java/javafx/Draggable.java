package javafx;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
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
    this.shape.setOnMousePressed(event -> movementDelta = new Point2D(shape.getLayoutX() - event.getSceneX(), shape
            .getLayoutY() - event.getSceneY()));
    shape.setOnMouseDragged(event -> {
      shape.setLayoutX(event.getSceneX() + movementDelta.getX());
      shape.setLayoutY(event.getSceneY() + movementDelta.getY());
      if(this.onDrag ==null) return;
      this.onDrag.handle(event);
    });
    shape.setOnMouseReleased(e->{
      if(e.getSceneY()>(parent.getHeight()-82) && e.getSceneX()>(parent.getWidth()-82)){
        parent.getChildren().remove(this.shape);
        this.onDestroy.handle(e);
      }
    });
  }
}
