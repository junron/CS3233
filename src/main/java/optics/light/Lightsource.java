package optics.light;

import utils.OpticsList;

public interface Lightsource {
  int maximumReflectionDepth = 50;
  void renderRays(OpticsList objects);
  void removeAllLines();
}
