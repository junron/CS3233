package javafx;

import application.Storage;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import optics.light.Ray;
import optics.light.RayCircle;
import optics.objects.OpticalRectangle;


public class Rotatable {
  private Shape shape;

  public Rotatable(Shape shape, EventHandler<KeyEvent> onRotate) {
    this.shape = shape;
    this.shape.setOnMouseClicked(event -> this.shape.requestFocus());
    this.shape.setOnKeyPressed(event -> {
      String eventCode = event.getCode().toString();
      if (event.isControlDown()) {
        if (eventCode.equals("D")) {
          if (shape instanceof OpticalRectangle) {
            OpticalRectangle newRectangle = ((OpticalRectangle) shape).clone(true);
            Storage.opticsTabController.addObject(newRectangle, ((OpticalRectangle) shape).getRealParent());
          } else if (shape instanceof RayCircle) {
            Ray newRay = ((RayCircle) shape).clone();
            Storage.rayTabController.createRay(newRay);
          }
        }
      }
      if (event.isShiftDown()) {
//        Move object instead of rotating it
        switch (eventCode) {
          case ("LEFT"): {
            if (this.shape instanceof Circle) {
              ((Circle) this.shape).setCenterX(((Circle) this.shape).getCenterX() - 1);
            } else if (this.shape instanceof Rectangle) {
              ((Rectangle) this.shape).setX(((Rectangle) this.shape).getX() - 1);
            }
            break;
          }
          case ("RIGHT"): {
            if (this.shape instanceof Circle) {
              ((Circle) this.shape).setCenterX(((Circle) this.shape).getCenterX() + 1);
            } else if (this.shape instanceof Rectangle) {
              ((Rectangle) this.shape).setX(((Rectangle) this.shape).getX() + 1);
            }
            break;
          }
          case ("UP"): {
            if (this.shape instanceof Circle) {
              ((Circle) this.shape).setCenterY(((Circle) this.shape).getCenterY() - 1);
            } else if (this.shape instanceof Rectangle) {
              ((Rectangle) this.shape).setY(((Rectangle) this.shape).getY() - 1);
            }
            break;
          }
          case ("DOWN"): {
            if (this.shape instanceof Circle) {
              ((Circle) this.shape).setCenterY(((Circle) this.shape).getCenterY() + 1);
            } else if (this.shape instanceof Rectangle) {
              ((Rectangle) this.shape).setY(((Rectangle) this.shape).getY() + 1);
            }
            break;
          }
          default:
            return;
        }
        onRotate.handle(event);
        return;
      }
      if (event.isAltDown()) {
//        Move object instead of rotating it
        switch (eventCode) {
          case ("LEFT"): {
            if (this.shape instanceof Rectangle) {
              ((Rectangle) this.shape).setWidth(((Rectangle) this.shape).getWidth() - 1);
            }
            break;
          }
          case ("RIGHT"): {
            if (this.shape instanceof Rectangle) {
              ((Rectangle) this.shape).setWidth(((Rectangle) this.shape).getWidth() + 1);
            }
            break;
          }
          case ("UP"): {
            if (this.shape instanceof Rectangle) {
              ((Rectangle) this.shape).setHeight(((Rectangle) this.shape).getHeight() + 1);
            }
            break;
          }
          case ("DOWN"): {
            if (this.shape instanceof Rectangle) {
              ((Rectangle) this.shape).setHeight(((Rectangle) this.shape).getHeight() - 1);
            }
            break;
          }
          default:
            return;
        }
        onRotate.handle(event);
        return;
      }
      double rotate = this.shape.getRotate();
      if (eventCode.equals("LEFT")) {
//        Rotate anticlockwise
        this.shape.setRotate(rotate - (event.isControlDown() ? 45 : 1));
      } else if (eventCode.equals("RIGHT")) {
        this.shape.setRotate(rotate + (event.isControlDown() ? 45 : 1));
      } else {
        return;
      }
      onRotate.handle(event);
    });
  }
}
