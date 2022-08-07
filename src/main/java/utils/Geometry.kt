package utils

import javafx.geometry.Point2D
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Path
import javafx.scene.shape.Shape
import math.Intersection
import math.Vectors
import optics.InteractiveOpticalRectangle
import optics.RealLine
import optics.objects.OpticalRectangle
import optics.objects.Refract
import java.lang.Double.MAX_VALUE

object Geometry {
    @JvmStatic
    fun createLineFromPoints(p1: Point2D, p2: Point2D): Line {
        return Line(p1.x, p1.y, p2.x, p2.y)
    }

    @JvmStatic
    fun createCircleFromPoint(p1: Point2D, radius: Double): Circle {
        return Circle(p1.x, p1.y, radius)
    }

    fun getNearestIntersection(
        l: RealLine,
        interactiveObjects: OpticsList<InteractiveOpticalRectangle>,
        currentObj: OpticalRectangle?,
    ): Pair<InteractiveOpticalRectangle?, Point2D?> {
        var minDist = Double.MAX_VALUE
        var minDistObj: InteractiveOpticalRectangle? = null
        var minDistIntersection: Point2D? = null
        interactiveObjects.forEach {
            val boundingLines = it.boundingLines()
            boundingLines.forEach { line ->
                val intersection =
                    line.intersectionPoint(l.start, l.end) ?: return@forEach
                val intersectionDistance = intersection.distance(l.start)
                if (intersectionDistance < minDist) {
                    minDist = intersectionDistance
                    minDistObj = it
                    minDistIntersection = intersection
                }
            }
        }
        return minDistObj to minDistIntersection
    }

    fun getNearestIntersection(
        l: RealLine,
        interactiveObjects: OpticsList<InteractiveOpticalRectangle>,
    ): Pair<InteractiveOpticalRectangle?, Point2D?> {
        return getNearestIntersection(l, interactiveObjects, null)
    }

    @JvmStatic
    @JvmOverloads
    fun fixAngle(angle: Double, decimalPlaces: Int = 1): String {
        var angle = angle
        angle %= 360.0
        if (angle < 0) angle += 360.0
        return String.format("%." + decimalPlaces + "f", angle)
    }

    @JvmOverloads
    fun fixAngleRadians(angle: Double, mod: Double = Math.PI * 2): Double {
        var angle = angle
        angle %= mod
        if (angle < 0) angle += mod
        return angle
    }
}
