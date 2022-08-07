package optics

import javafx.geometry.Point2D
import optics.objects.Interactive
import optics.objects.OpticalRectangle
import utils.toRadians
import utils.toRealPoint
import kotlin.math.cos
import kotlin.math.sin

abstract class InteractiveOpticalRectangle(
    x: Double,
    y: Double, width: Double,
    height: Double,
    val rotation: Double,
) : OpticalRectangle(x, y, width, height),
    Interactive<InteractiveOpticalRectangle> {
    val angle = rotation.toRadians()
    fun center(): Point2D {
        return Point2D(x + width / 2, y + height / 2)
    }

    fun boundingBox(): List<Point2D> {
        val topLeft = Point2D(x, y)
        return listOf(topLeft,
            topLeft.add(width, 0.0),
            topLeft.add(width, height),
            topLeft.add(0.0, height)).map {
            translateAboutPoint(it,
                center(),
                angle)
        }
    }

    fun realBoundingBox() = boundingBox().map { it.toRealPoint() }
    fun boundingLines(): List<RealLine> {
        val boundingBox = realBoundingBox()
        return listOf(
            RealLine(boundingBox[0], boundingBox[1]),
            RealLine(boundingBox[1], boundingBox[2]),
            RealLine(boundingBox[2], boundingBox[3]),
            RealLine(boundingBox[3], boundingBox[0])
        )
    }

    private fun translateAboutPoint(
        a: Point2D,
        b: Point2D,
        angle: Double,
    ): Point2D {
        val c = cos(angle)
        val s = sin(angle)
        val dx = a.x - b.x
        val dy = a.y - b.y
        val x = c * dx - s * dy + b.x
        val y = s * dx + c * dy + b.y
        return Point2D(x, y)
    }
}
