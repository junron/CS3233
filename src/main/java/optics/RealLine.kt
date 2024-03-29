package optics

import javafx.geometry.Point2D
import javafx.scene.shape.Line
import utils.minus
import utils.plus
import utils.times
import utils.toScreenPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class RealLine(val start: Point2D, val end: Point2D) {

    val screenStart = start.toScreenPoint()
    val screenEnd = end.toScreenPoint()
    val m = (start.y - end.y) / (start.x - end.x)
    val c = start.y - m * start.x
    val magnitude = (end - start).magnitude()
    val angle = atan2((end - start).y, (end - start).x)

    val screenLine =
        Line(screenStart.x, screenStart.y, screenEnd.x, screenEnd.y)

    fun passesThrough(point: Point2D) = (point.x * m + c) == point.y

    // https://stackoverflow.com/a/9997374/11168593
    fun intersects(lineStart: Point2D, lineEnd: Point2D) =
        ccw(start, lineStart, lineEnd) != ccw(end, lineStart, lineEnd) &&
            ccw(start, end, lineStart) != ccw(start, end, lineEnd)

    // xm_1 + c1 = xm2 + c2
    // x(m1 - m2) = c2 - c1
    // x = (c2-c1)/(m1-m2)
    fun intersectionPoint(lineStart: Point2D, lineEnd: Point2D): Point2D? {
        if (!intersects(lineStart, lineEnd)) return null
        val line2 = RealLine(lineStart, lineEnd)
        val x = (line2.c - c) / (m - line2.m)
        return Point2D(x, m * x + c)
    }

    // Radians
    fun copyWithAngle(angle: Double): RealLine {
        val newEnd = Point2D(cos(angle), sin(angle)) * magnitude + start
        return RealLine(start, newEnd)
    }
}

fun ccw(A: Point2D, B: Point2D, C: Point2D): Boolean {
    return (C.y - A.y) * (B.x - A.x) > (B.y - A.y) * (C.x - A.x)
}
