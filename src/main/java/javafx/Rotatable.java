package javafx;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;


public class Rotatable {
  private Rectangle shape;

  public Rotatable(Rectangle shape, EventHandler<KeyEvent> onRotate) {
    this.shape = shape;
    this.shape.setOnMouseClicked(event -> this.shape.requestFocus());
    this.shape.setOnKeyPressed(event -> {
      String eventCode = event.getCode().toString();
      if(event.isShiftDown()){
//        Move object instead of rotating it
        switch(eventCode){
          case("LEFT"):{
            this.shape.setX(this.shape.getX()-1);
            break;
          }
          case("RIGHT"):{
            this.shape.setX(this.shape.getX()+1);
            break;
          }
          case("UP"):{
            this.shape.setY(this.shape.getY()-1);
            break;
          }
          case("DOWN"):{
            this.shape.setY(this.shape.getY()+1);
            break;
          }
          default: return;
        }
        onRotate.handle(event);
        return;
      }
      double rotate = this.shape.getRotate();
      if(eventCode.equals("LEFT")){
//        Rotate anticlock
        this.shape.setRotate(rotate-(event.isControlDown()?45:1));
      }else if(eventCode.equals("RIGHT")){
        this.shape.setRotate(rotate+(event.isControlDown()?45:1));
      }else{
        return;
      }
      onRotate.handle(event);
    });
  }
}
