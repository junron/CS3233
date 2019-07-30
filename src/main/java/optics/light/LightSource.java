package optics.light;

import optics.objects.OpticalRectangle;
import utils.OpticsList;

public interface LightSource {
  int maximumReflectionDepth = 50;
  void renderRays(OpticsList<OpticalRectangle> objects);
  void removeAllLines();
}
