package optics.light;

import optics.objects.OpticalRectangle;
import utils.OpticsList;

import java.util.concurrent.CompletableFuture;

public interface LightSource {
  CompletableFuture renderRays(OpticsList<OpticalRectangle> objects);

  void removeAllLines();
}
