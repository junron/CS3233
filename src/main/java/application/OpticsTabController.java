package application;

import javafx.SettableTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import networking.NetworkingClient;
import optics.objects.Mirror;
import optics.objects.OpticalRectangle;
import optics.objects.Refract;
import optics.objects.Wall;

import static application.Storage.opticalRectangles;
import static application.Storage.reRenderAll;
import static utils.Geometry.fixAngle;

public class OpticsTabController {

  @FXML
  private Button newWall;
  @FXML
  private Button newMirror;
  @FXML
  private Button newRefractor;
  @FXML
  private SettableTextField refractiveIndex, width, rotation, height;

  private OpticalRectangle focusedObject;

  void initialize(Pane parent) {
    newMirror.setOnMouseClicked(event -> {
      // Prevent changes when animating
      if (Storage.isAnimating) return;
      Mirror m = new Mirror(parent.getWidth() / 2, parent.getHeight() / 2 - 100, 20, 200, parent, 0);
      addObject(m, parent);
    });
    newWall.setOnMouseClicked(event -> {
      // Prevent changes when animating
      if (Storage.isAnimating) return;
      Wall w = new Wall(parent.getWidth() / 2, parent.getHeight() / 2 - 25, 20, 50, parent, 0);
      addObject(w, parent);
    });
    newRefractor.setOnMouseClicked(event -> {
      // Prevent changes when animating
      if (Storage.isAnimating) return;
      Refract re = new Refract(parent.getWidth() / 2, parent.getHeight() / 2 - 50, 20, 100, parent, 0, 1);
      addObject(re, parent);
    });

    rotation.textProperty().addListener((o, ol, val) -> {
      // Prevent changes when animating
      if (Storage.isAnimating) return;
      if (this.focusedObject == null) return;
      if (val.length() == 0) {
        this.focusedObject.setRotate(0);
        reRenderAll();
        return;
      }
      Double value = validate(val, false);
      if (value == null) return;
      this.focusedObject.setRotate(Double.parseDouble(fixAngle(value)));
      reRenderAll();
    });

    width.setChangeListener((o, ol, val) -> {
      // Prevent changes when animating
      if (Storage.isAnimating) return;
      if (this.focusedObject == null) return;
      if (val.length() == 0) {
        this.focusedObject.setWidth(5);
        reRenderAll();
        return;
      }
      Double value = validate(val, true);
      if (value == null) return;
      this.focusedObject.setWidthChecked(value);
      reRenderAll();
    });

    height.textProperty().addListener((o, ol, val) -> {
      // Prevent changes when animating
      if (Storage.isAnimating) return;
      if (this.focusedObject == null) return;
      if (val.length() == 0) {
        this.focusedObject.setHeight(5);
        reRenderAll();
        return;
      }
      Double value = validate(val, true);
      if (value == null) return;
      this.focusedObject.setHeightChecked(value);
      reRenderAll();
    });

    refractiveIndex.setChangeListener((o, ol, val) -> {
      // Prevent changes when animating
      if (Storage.isAnimating) return;
      if (this.focusedObject == null) return;
      if (!(this.focusedObject instanceof Refract)) return;
      Refract object = (Refract) this.focusedObject;
      if (val.length() == 0) {
        object.setRefractiveIndex(1);
        reRenderAll();
        return;
      }
      Double value = validate(val, true);
      if (value == null) return;
      if (value < 1) return;
      object.setRefractiveIndex(value);
      reRenderAll();
    });
  }

  public void addObject(OpticalRectangle object, Pane parent, boolean syncToServer) {
    opticalRectangles.add(object);
    object.addOnStateChange(event1 -> {
      NetworkingClient.updateObject(object, opticalRectangles.indexOf(object));
      changeFocus(object);
      reRenderAll();
    });
    object.setOnDestroy(e -> {
      if (this.focusedObject == object) this.focusedObject = null;
      rotation.setText("-");
      height.setText("-");
      width.setText("-");
      refractiveIndex.setText("-");
      NetworkingClient.removeObject("", opticalRectangles.indexOf(object));
      opticalRectangles.remove(object);
      reRenderAll();
      return null;
    });
    object.focusedProperty().addListener((o, ol, state) -> {
      if (state) changeFocus(object);
    });
    if (syncToServer) NetworkingClient.addObject(object);
    reRenderAll();
    parent.getChildren().add(object);
    object.requestFocus();
    changeFocus(object);
  }

  private void addObject(OpticalRectangle object, Pane parent) {
    addObject(object, parent, true);
  }

  private void changeFocus(OpticalRectangle object) {
    if (this.focusedObject != null) {
      this.focusedObject.setStroke(Color.BLACK);
      this.focusedObject.setStrokeWidth(1);
    }
    this.focusedObject = object;
    this.focusedObject.setStroke(Color.BLUE);
    this.focusedObject.setStrokeWidth(2);
    rotation.setText(fixAngle(object.getRotate()));
    width.setText(String.valueOf(object.getWidth()));
    height.setText(String.valueOf(object.getHeight()));
    if (object instanceof Refract) refractiveIndex.setText(String.valueOf(((Refract) object).getRefractiveIndex()));
  }

  private Double validate(String value, boolean positive) {
    double res;
    try {
      res = Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return null;
    }
    if (positive && res < 0) return null;
    return res;
  }
}


