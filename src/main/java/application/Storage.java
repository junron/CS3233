package application;

import optics.light.Ray;
import optics.objects.OpticalRectangle;
import utils.OpticsList;

import java.util.ArrayList;

public class Storage {
  static final ArrayList<Ray> rays = new ArrayList<>();
  static final OpticsList<OpticalRectangle> opticalRectangles = new OpticsList<>();
  static void reRenderAll(){
    for (Ray r : rays){
      r.renderRays(opticalRectangles);
    }
  }
  public static OpticsTabController opticsTabController;
}
