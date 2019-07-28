package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import optics.light.Ray;
import optics.objects.Mirror;
import optics.objects.Wall;
import serialize.FileOps;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static application.Storage.*;

public class GeneralTabController {

  @FXML
  private Button save,load,clearAll;

  public void initialize(Pane parent) {
    save.setOnMouseClicked(event -> {
      ArrayList<Object> allObjects = new ArrayList<Object>();
      allObjects.addAll(opticalRectangles);
      allObjects.addAll(rays);
      try {
        FileOps.save(allObjects,(Stage)parent.getScene().getWindow());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    load.setOnMouseClicked(e->{
      ArrayList<byte[]> data;
      try {
        data = FileOps.load((Stage)parent.getScene().getWindow());
      } catch (IOException ex) {
        ex.printStackTrace();
        return;
      }
      if(data==null) return;
      for(byte[] object:data){
        ByteBuffer buffer = ByteBuffer.wrap(object);
        switch (buffer.getChar()){
          case 'm':{
            Mirror m = new Mirror(0,0,0,0,parent,0);
            m.deserialize(object);
            addObject(m,parent);
            break;
          }
          case 'w':{
            Wall w = new Wall(0,0,0,0,parent,0);
            w.deserialize(object);
            addObject(w,parent);
            break;
          }
          case 'r':{
            Ray r = new Ray(new Line(),parent);
            r.deserialize(object);
            rays.add(r);
            r.setOnDestroy(ev->{
              rays.remove(r);
              return null;
            });
            r.addOnStateChange(ev-> r.renderRays(opticalRectangles));
            r.renderRays(opticalRectangles);
            break;
          }
        }
      }
    });

    clearAll.setOnMouseClicked(event->{
      parent.getChildren().removeAll(opticalRectangles);
      opticalRectangles.clear();
      for(Ray r: rays){
        r.destroy();
      }
      rays.clear();
      rerenderAll();
    });
  }
}

