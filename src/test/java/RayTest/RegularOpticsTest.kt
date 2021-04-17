package RayTest

import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.shape.Path
import javafx.scene.shape.Shape
import junit.framework.TestCase.assertEquals
import math.Intersection
import math.Vectors
import optics.PreciseJavaFXLine
import optics.light.Ray
import optics.objects.Mirror
import org.junit.Test
import utils.Geometry


class RegularOpticsTest {

  @Test
  fun zeroDegTest() {
    val pane: Pane = AnchorPane()
    val origin = Vectors(10.0, 150.0)
    val m = Mirror(100.0, 100.0, 20.0, 200.0, pane, 0.0)
    val r = Ray(PreciseJavaFXLine(Geometry.createLineFromPoints(origin, origin
            .add(Vectors.constructWithMagnitude(0.0, 2000.0)))), pane)
    val intersection = Shape.intersect(r.currentJavaFXLine, m) as Path
    val iPoint = Intersection.getIntersectionPoint(intersection, origin, false)
    val tData = m.transform(r, iPoint)!!
    assertEquals(0.0, Math.toDegrees(tData.intersectionSideData.normalAngle), 1E-5)
    assertEquals(180.0, Math.toDegrees(tData.preciseJavaFXLine.preciseAngle), 1E-5)
  }

  @Test
  fun zeroDegBothRotatedTest() {
    val pane: Pane = AnchorPane()
    val origin = Vectors(10.0, 150.0)
    val m = Mirror(100.0, 100.0, 20.0, 200.0, pane, 10.0)
    val r = Ray(PreciseJavaFXLine(Geometry.createLineFromPoints(origin, origin
            .add(Vectors.constructWithMagnitude(Math.toRadians(10.0), 2000.0)))), pane)
    val intersection = Shape.intersect(r.currentJavaFXLine, m) as Path
    val iPoint = Intersection.getIntersectionPoint(intersection, origin, false)
    val tData = m.transform(r, iPoint)!!
    assertEquals(10.0, Math.toDegrees(tData.intersectionSideData.normalAngle), 1E-5)
    assertEquals(200.0, Math.toDegrees(tData.preciseJavaFXLine.preciseAngle), 1E-5)
  }
}
