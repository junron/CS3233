package javafx;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;


public class Rotatable {
  private Shape shape;

  public Rotatable(Shape shape, EventHandler<KeyEvent> onRotate) {
    this.shape = shape;
    this.shape.setOnMouseClicked(event -> this.shape.requestFocus());
    this.shape.setOnKeyPressed(event -> {
      String eventCode = event.getCode().toString();
      if(event.isShiftDown()){
//        Move object instead of rotating it
        switch(eventCode){
          case("LEFT"):{
            this.shape.setLayoutX(this.shape.getLayoutX()-1);
            break;
          }
          case("RIGHT"):{
            this.shape.setLayoutX(this.shape.getLayoutX()+1);
            break;
          }
          case("UP"):{
            this.shape.setLayoutY(this.shape.getLayoutY()-1);
            break;
          }
          case("DOWN"):{
            this.shape.setLayoutY(this.shape.getLayoutY()+1);
            break;
          }
          default: return;
        }
        onRotate.handle(event);
        return;
      }
      if(event.isAltDown()){
//        Move object instead of rotating it
        switch(eventCode){
          case("LEFT"):{
            if(this.shape instanceof Rectangle){
              ((Rectangle) this.shape).setWidth(((Rectangle) this.shape).getWidth()-1);
            }
            break;
          }
          case("RIGHT"):{
            if(this.shape instanceof Rectangle){
              ((Rectangle) this.shape).setWidth(((Rectangle) this.shape).getWidth()+1);
            }
            break;
          }
          case("UP"):{
            if(this.shape instanceof Rectangle){
              ((Rectangle) this.shape).setHeight(((Rectangle) this.shape).getHeight()+1);
            }
            break;
          }
          case("DOWN"):{
            if(this.shape instanceof Rectangle){
              ((Rectangle) this.shape).setHeight(((Rectangle) this.shape).getHeight()-1);
            }
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
