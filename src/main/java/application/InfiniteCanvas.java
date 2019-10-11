package application;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class InfiniteCanvas extends AnchorPane {
  private Point2D offset = new Point2D(0, 0);
  private Point2D startPoint = new Point2D(0, 0);
  private Point2D oldOffset = offset;
  private long previousChange = 0;

  public InfiniteCanvas(int delay) {
    this.setOnMousePressed(event -> startPoint = new Point2D(event.getSceneX(), event.getSceneY()));
    this.setOnMouseDragged(event -> {
      offset = offset.add(new Point2D(event.getSceneX(), event.getSceneY()).subtract(startPoint));
      startPoint = new Point2D(event.getSceneX(), event.getSceneY());
      if (System.currentTimeMillis() - previousChange < delay) return;
      for (Node child : this.getChildren()) {
        if (child instanceof CanvasNode) {
          ((CanvasNode) child).reposition(oldOffset, offset);
        }
      }
      oldOffset = offset;
      previousChange = System.currentTimeMillis();
    });
  }

  public InfiniteCanvas() {
    this(10);
  }
}
