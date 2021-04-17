package javafx;

import application.Storage;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import optics.light.Ray;
import optics.light.RayCircle;
import optics.objects.OpticalRectangle;
import optics.objects.Refract;


public class KeyActions {
  private Shape shape;

  public KeyActions(Shape shape, EventHandler<KeyEvent> onRotate, EventHandler<Event> onDestroy, Pane parent) {
    this.shape = shape;
    this.shape.setOnMouseClicked(event -> this.shape.requestFocus());
    this.shape.setOnKeyPressed(event -> {
      // Prevent changes when animating
      if (Storage.isAnimating) return;
      String eventCode = event.getCode().toString();
      if (eventCode.equals("DELETE")) {
        parent.getChildren().remove(this.shape);
        onDestroy.handle(event);
      }
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
      if (this.shape instanceof Refract) {
        Refract object = (Refract) this.shape;
        if (eventCode.equals("ADD") || event.isShiftDown() && eventCode.equals("EQUALS")) {
          object.setRefractiveIndex(object.getRefractiveIndex() + 0.01);
          onRotate.handle(event);
          return;
        } else if (eventCode.equals("SUBTRACT") || eventCode.equals("MINUS")) {
          object.setRefractiveIndex(object.getRefractiveIndex() - 0.01);
          onRotate.handle(event);
          return;
        }
      }
      if (event.isShiftDown()) {
        //        Move object instead of rotating it
        switch (eventCode) {
          case ("LEFT"): {
            if (this.shape instanceof RayCircle) {
              Ray r = ((RayCircle) this.shape).getRay();
              r.setScreenX(((RayCircle) this.shape).getCenterX() - 1);
            } else if (this.shape instanceof OpticalRectangle) {
              ((OpticalRectangle) this.shape).setScreenX(((OpticalRectangle) this.shape).getX() - 1);
            }
            break;
          }
          case ("RIGHT"): {
            if (this.shape instanceof RayCircle) {
              Ray r = ((RayCircle) this.shape).getRay();
              r.setScreenX(((RayCircle) this.shape).getCenterX() + 1);
            } else if (this.shape instanceof OpticalRectangle) {
              ((OpticalRectangle) this.shape).setScreenX(((OpticalRectangle) this.shape).getX() + 1);
            }
            break;
          }
          case ("UP"): {
            if (this.shape instanceof RayCircle) {
              Ray r = ((RayCircle) this.shape).getRay();
              r.setScreenY(((RayCircle) this.shape).getCenterY() - 1);
            } else if (this.shape instanceof OpticalRectangle) {
              ((OpticalRectangle) this.shape).setScreenY(((OpticalRectangle) this.shape).getY() - 1);
            }
            break;
          }
          case ("DOWN"): {
            if (this.shape instanceof RayCircle) {
              Ray r = ((RayCircle) this.shape).getRay();
              r.setScreenY(((RayCircle) this.shape).getCenterY() + 1);
            } else if (this.shape instanceof OpticalRectangle) {
              ((OpticalRectangle) this.shape).setScreenY(((OpticalRectangle) this.shape).getY() + 1);
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
        OpticalRectangle optShape;
        if (this.shape instanceof OpticalRectangle) {
          optShape = (OpticalRectangle) this.shape;
        } else {
          onRotate.handle(event);
          return;
        }
        switch (eventCode) {
          case ("LEFT"): {
            optShape.setWidthChecked(optShape.getWidth() - 1);
            break;
          }
          case ("RIGHT"): {
            optShape.setWidthChecked(optShape.getWidth() + 1);
            break;
          }
          case ("UP"): {
            optShape.setHeightChecked(optShape.getHeight() + 1);
            break;
          }
          case ("DOWN"): {
            optShape.setHeightChecked(optShape.getHeight() - 1);
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
        this.shape.setRotate((rotate - (event.isControlDown() ? 45 : 1)) % 360);
      } else if (eventCode.equals("RIGHT")) {
        //        Clockwise
        this.shape.setRotate((rotate + (event.isControlDown() ? 45 : 1)) % 360);
      } else if (eventCode.equals("UP") && event.isControlDown()) {
        this.shape.setRotate((360 - rotate) % 360);
      } else if (eventCode.equals("DOWN") && event.isControlDown()) {
        this.shape.setRotate((rotate - 180) % 360);
      } else {
        return;
      }
      onRotate.handle(event);
    });
  }
}
