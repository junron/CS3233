package javafx;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;

public class Rotatable {
  private Rectangle shape;
  private EventHandler<KeyEvent> onRotate;

  public Rotatable(Rectangle shape, EventHandler<KeyEvent> onRotate) {
    this.shape = shape;
    this.onRotate = onRotate;
    this.shape.setOnMouseClicked(event -> this.shape.requestFocus());
    this.shape.setOnKeyPressed(event -> {
      KeyCode eventCode = event.getCode();
      double rotate = this.shape.getRotate();
      if(eventCode.equals(KeyCode.A)){
//        Rotate anticlock
        this.shape.setRotate(rotate-1);
      }else if(eventCode.equals(KeyCode.D)){
        this.shape.setRotate(rotate+1);
      }else{
        return;
      }
      onRotate.handle(event);
    });
  }
}
