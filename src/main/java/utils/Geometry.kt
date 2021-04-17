package utils

import javafx.geometry.Point2D
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Path
import javafx.scene.shape.Shape
import math.Intersection
import math.Vectors
import optics.InteractiveOpticalRectangle
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
        l: Line, interactiveObjects: OpticsList<InteractiveOpticalRectangle>,
        currentObj: OpticalRectangle?
    ): InteractiveOpticalRectangle? {
        //    Intersection point must be at least 1 px away from origin
        val hasObj = currentObj != null
        val threshold = 4.0
        val origin = Vectors(l.startX, l.startY)
        var result: InteractiveOpticalRectangle? = null
        var bestIntersectionDistance: Double = MAX_VALUE
        for (i in interactiveObjects) {
            val isCurrObj = hasObj && i == currentObj
            if (isCurrObj && i !is Refract) continue
            val intersection = Shape.intersect(l, i) as Path
            if (Intersection.hasIntersectionPoint(intersection)) {
                val iPoint = Intersection.getIntersectionPoint(intersection,
                    origin,
                    !isCurrObj)
                val distance = Vectors.distanceSquared(iPoint, origin)
                if (distance < threshold) {
                    continue
                }
                if (distance < bestIntersectionDistance) {
                    bestIntersectionDistance = distance
                    result = i
                }
            }
        }
        return result
    }

    fun getNearestIntersection(
        l: Line,
        interactiveObjects: OpticsList<InteractiveOpticalRectangle>
    ): InteractiveOpticalRectangle? {
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
