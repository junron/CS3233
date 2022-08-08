package utils

import javafx.geometry.Point2D
import javafx.scene.shape.Circle
import javafx.scene.shape.Line

object Geometry {
    @JvmStatic
    fun createLineFromPoints(p1: Point2D, p2: Point2D): Line {
        return Line(p1.x, p1.y, p2.x, p2.y)
    }

    @JvmStatic
    fun createCircleFromPoint(p1: Point2D, radius: Double): Circle {
        return Circle(p1.x, p1.y, radius)
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
