package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import optics.PreciseLine;
import optics.light.Ray;

import static application.Storage.opticalRectangles;
import static application.Storage.rays;

public class RayTabController{

  @FXML
  private Button newRay;
  @FXML
  private TextField rayRotation;
  @FXML
  private ColorPicker rayColor;

  private Ray focusedRay;
  private String expectedText;
  private Color expectedColor;
  private Pane parent;

  public void initialize(Pane parent) {
    this.parent = parent;
    newRay.setOnMouseClicked(e->{
      PreciseLine l = new PreciseLine(parent.getWidth()/2, parent.getHeight()/2,parent.getWidth()/2+2500, parent.getHeight()/2);
      l.setPreciseAngle(0);
      Ray r = new Ray(l,parent);
      this.createRay(r);
    });
    rayRotation.textProperty().addListener((o,ol,val)->{
      if(val.equals(expectedText)) return;
      if(val.length()==0){
        if(this.focusedRay==null) return;
        this.focusedRay.setAngle(0);
        this.focusedRay.renderRays(opticalRectangles);
        return;
      }
      double value;
      try{
        value = Double.parseDouble(val);
      }catch (NumberFormatException e){
        return;
      }
      if(this.focusedRay==null) return;
      this.focusedRay.setAngle(Double.parseDouble(fixAngle(value)));
      this.focusedRay.renderRays(opticalRectangles);
    });
    rayColor.valueProperty().addListener((o,ol,color)->{
      if(this.focusedRay==null) return;
      if(color.equals(expectedColor)) return;
      this.focusedRay.setColor(color);
      this.focusedRay.renderRays(opticalRectangles);
    });
  }
  public void createRay(Ray r){
    rays.add(r);
    r.setOnDestroy(e->{
      rays.remove(r);
      return null;
    });
    r.addOnStateChange(e-> {
      r.renderRays(opticalRectangles);
      this.expectedText = fixAngle(r.getAngle());
      this.expectedColor = this.focusedRay.getColor();
      rayColor.setValue(this.focusedRay.getColor());
      rayRotation.setText(expectedText);
    });
    r.setOnFocusStateChanged(state -> {
      if(state){
        this.focusedRay = r;
        this.expectedText = fixAngle(r.getAngle());
        this.expectedColor = this.focusedRay.getColor();
        rayColor.setValue(this.focusedRay.getColor());
        rayRotation.setText(expectedText);
      }
      return null;
    });
    r.renderRays(opticalRectangles);
    this.expectedText = fixAngle(r.getAngle());
    this.focusedRay = r;
    this.expectedColor = Color.BLACK;
    rayRotation.setText(expectedText);
    rayColor.valueProperty().setValue(Color.BLACK);
    r.requestFocus();
  }
  private String fixAngle(double angle){
    angle %=360;
    if(angle<0) angle+=360;
    return String.valueOf(angle);
  }
}

