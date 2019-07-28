package optics.light;

import utils.OpticsList;

public interface LightSource {
  int maximumReflectionDepth = 50;
  void renderRays(OpticsList objects);
  void removeAllLines();
}
