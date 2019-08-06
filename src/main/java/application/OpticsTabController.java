package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import optics.objects.Mirror;
import optics.objects.OpticalRectangle;
import optics.objects.Refract;
import optics.objects.Wall;

import static application.Storage.*;
import static utils.Geometry.fixAngle;

public class OpticsTabController{

  @FXML
  private Button newWall;
  @FXML
  private Button newMirror;
  @FXML
  private Button newRefractor;
  @FXML
  private TextField rotation,width,height;

  private OpticalRectangle focusedObject;
  private String expectedText;

  void initialize(Pane parent) {
    newMirror.setOnMouseClicked(event -> {
      Mirror m = new Mirror(parent.getWidth()/2, parent.getHeight()/2-100, 20, 200, parent, 0);
      addObject(m,parent);
    });
    newWall.setOnMouseClicked(event -> {
      Wall w = new Wall(parent.getWidth()/2, parent.getHeight()/2-25, 20, 50, parent, 0);
      addObject(w,parent);
    });
    newRefractor.setOnMouseClicked(event -> {
      Refract re = new Refract(parent.getWidth()/2, parent.getHeight()/2-50, 20, 100, parent, 0,1.42);
      addObject(re,parent);
    });

    rotation.textProperty().addListener((o,ol,val)->{
      if(val.equals(expectedText.split(" ")[0])) return;
      if(this.focusedObject==null) return;
      if(val.length()==0){
        this.focusedObject.setRotate(0);
        reRenderAll();
        return;
      }
      Double value = validate(val,false);
      if(value==null) return;
      this.focusedObject.setRotate(Double.parseDouble(fixAngle(value)));
      reRenderAll();
    });

    width.textProperty().addListener((o,ol,val)->{
      if(val.equals(expectedText.split(" ")[1])) return;
      if(this.focusedObject==null) return;
      if(val.length()==0){
        this.focusedObject.setWidth(1);
        reRenderAll();
        return;
      }
      Double value = validate(val,true);
      if(value==null) return;
      this.focusedObject.setWidth(value);
      reRenderAll();
    });

    height.textProperty().addListener((o,ol,val)->{
      if(val.equals(expectedText.split(" ")[2])) return;
      if(this.focusedObject==null) return;
      if(val.length()==0){
        this.focusedObject.setHeight(1);
        reRenderAll();
        return;
      }
      Double value = validate(val,true);
      if(value==null) return;
      this.focusedObject.setHeight(value);
      reRenderAll();
    });
  }
  public void addObject(OpticalRectangle object, Pane parent){
    opticalRectangles.add(object);
    object.addOnStateChange(event1 -> {
      this.focusedObject = object;
      this.expectedText = fixAngle(object.getRotate())+" "+object.getWidth()+" "+object.getHeight();
      rotation.setText(fixAngle(object.getRotate()));
      width.setText(String.valueOf(object.getWidth()));
      height.setText(String.valueOf(object.getHeight()));
      reRenderAll();
    });
    object.setOnDestroy(e -> {
      if(this.focusedObject==object) this.focusedObject = null;
      this.expectedText = "- - -";
      rotation.setText("-");
      height.setText("-");
      height.setText("-");
      opticalRectangles.remove(object);
      reRenderAll();
      return null;
    });
    object.focusedProperty().addListener((o,ol,state)->{
      if(state){
        this.focusedObject = object;
        this.expectedText = fixAngle(object.getRotate())+" "+object.getWidth()+" "+object.getHeight();
        rotation.setText(fixAngle(object.getRotate()));
        width.setText(String.valueOf(object.getWidth()));
        height.setText(String.valueOf(object.getHeight()));
      }
    });
    object.requestFocus();
    reRenderAll();
    parent.getChildren().add(object);
  }

  private Double validate(String value,boolean positive){
    double res;
    try{
      res = Double.parseDouble(value);
    }catch (NumberFormatException e){
      return null;
    }
    if(positive && res<0) return null;
    return res;

  }
}


