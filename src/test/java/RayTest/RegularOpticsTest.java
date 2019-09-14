package RayTest;

import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import math.Intersection;
import math.Vectors;
import optics.PreciseLine;
import optics.TransformData;
import optics.light.Ray;
import optics.objects.Mirror;
import org.junit.Test;
import utils.Geometry;

import static junit.framework.TestCase.assertEquals;


public class RegularOpticsTest {

  @Test
  public void zeroDegTest() {
    AnchorPane pane = new AnchorPane();
    Vectors origin = new Vectors(10, 150);
    Mirror m = new Mirror(100, 100, 20, 200, pane, 1);
    Ray r = new Ray(new PreciseLine(Geometry
            .createLineFromPoints(origin, origin.add(Vectors.constructWithMagnitude(Math.toRadians(10), 2000)))), pane);
    Path intersection = (Path) Shape.intersect(r.getCurrentLine(), m);
    Point2D iPoint = Intersection.getIntersectionPoint(intersection, new Vectors(10, 150), false);
    TransformData tData = m.transform(r, iPoint);
    assertEquals(0.0, Math.toDegrees(tData.getIntersectionSideData().normalAngle));
  }
}
