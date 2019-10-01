package serialize;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import optics.PreciseLine;
import optics.light.Ray;
import optics.objects.Mirror;
import optics.objects.OpticalRectangle;
import optics.objects.Refract;
import optics.objects.Wall;

import java.nio.ByteBuffer;

import static application.Storage.opticsTabController;
import static application.Storage.rayTabController;

public class Deserialize {
  public static Serializable deserialize(byte[] object, Pane parent) {
    ByteBuffer buffer = ByteBuffer.wrap(object);
    switch (buffer.getChar()) {
      case 'm': {
        Mirror m = new Mirror(0, 0, 0, 0, parent, 0);
        m.deserialize(object);
        return m;
      }
      case 'w': {
        Wall w = new Wall(0, 0, 0, 0, parent, 0);
        w.deserialize(object);
        return w;
      }
      case 'e': {
        Refract re = new Refract(0, 0, 0, 0, parent, 0, 1);
        re.deserialize(object);
        return re;
      }
      case 'r': {
        Ray r = new Ray(new PreciseLine(new Line()), parent);
        r.deserialize(object);
        return r;
      }
      default:
        return null;
    }
  }

  public static void deserializeAndAdd(byte[] object, Pane parent) {
    Serializable serializable = deserialize(object, parent);
    if (serializable instanceof Ray) {
      rayTabController.createRay((Ray) serializable);
    } else if (serializable instanceof OpticalRectangle) {
      opticsTabController.addObject((OpticalRectangle) serializable, parent);
    }
  }
}
