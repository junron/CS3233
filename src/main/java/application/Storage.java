package application;

import javafx.scene.layout.Pane;
import optics.light.Ray;
import optics.objects.OpticalRectangle;
import utils.OpticsList;

import java.util.ArrayList;

public class Storage {
  public static final ArrayList<Ray> rays = new ArrayList<>();
  public static final OpticsList<OpticalRectangle> opticalRectangles = new OpticsList<>();
  public static void addObject(OpticalRectangle object, Pane parent){
    opticalRectangles.add(object);
    object.addOnStateChange(event1 -> {
      reRenderAll();
    });
    object.setOnDestroy(e -> {
      opticalRectangles.remove(object);
      reRenderAll();
      return null;
    });
    reRenderAll();
    parent.getChildren().add(object);
  }
  public static void reRenderAll(){
    for (Ray r : rays){
      r.renderRays(opticalRectangles);
    }
  }
}
