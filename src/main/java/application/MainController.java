package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import optics.light.Ray;
import optics.objects.Mirror;
import optics.objects.OpticalRectangle;
import serialize.FileOps;
import utils.OpticsList;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

  private ArrayList<Ray> rays = new ArrayList<>();
  private OpticsList<OpticalRectangle> mirrors = new OpticsList<>();

  @FXML
  private AnchorPane parent;

  @FXML
  private Button newMirror;
  @FXML
  private Button newRay;

  @FXML
  private Button saveBtn;
  @FXML
  private Button load;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    newMirror.setOnMouseClicked(event -> {
      Mirror m = new Mirror(parent.getWidth()/2, parent.getHeight()/2, 14, 200, parent, 0);
      addObject(m);
    });
    newRay.setOnMouseClicked(event -> {
      Line l = new Line(parent.getWidth()/2, parent.getHeight()/2,parent.getWidth()/2+2500, parent.getHeight()/2);
      Ray r = new Ray(l,parent);
      rays.add(r);
      r.setOnDestroy(e->{
        rays.remove(r);
        parent.getChildren().remove(l);
        return null;
      });
      r.addOnStateChange(e-> r.renderRays(mirrors));
      r.renderRays(mirrors);
    });
    saveBtn.setOnMouseClicked(event -> {
      try {
        FileOps.save(mirrors,(Stage)parent.getScene().getWindow());
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
        Mirror m = new Mirror(0,0,0,0,parent,0);
        m.deserialize(object);
        addObject(m);
      }
    });
    parent.getChildren().addAll(mirrors);
  }

  private void addObject(Mirror object){
    this.mirrors.add(object);
    object.addOnStateChange(event1 -> {
      for (Ray r : rays){
        r.renderRays(mirrors);
      }
    });
    object.setOnDestroy(e -> {
      mirrors.remove(object);
      for (Ray r : rays){
        r.renderRays(mirrors);
      }
      return null;
    });
    for (Ray r : rays){
      r.renderRays(mirrors);
    }
    parent.getChildren().add(object);
  }
}

