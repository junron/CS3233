package optics.light;

import optics.objects.OpticalRectangle;
import utils.OpticsList;

public interface LightSource {
  void renderRays(OpticsList<OpticalRectangle> objects);
  void removeAllLines();
}
