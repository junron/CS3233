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

    fun passesThrough(point: Point2D): Boolean {
        if (m.isInfinite()) {
            return point.x == start.x
        }
        return (point.x * m + c) == point.y
    }

    // https://stackoverflow.com/a/9997374/11168593
    private fun intersects(lineStart: Point2D, lineEnd: Point2D) =
        ccw(start, lineStart, lineEnd) != ccw(end, lineStart, lineEnd) &&
            ccw(start, end, lineStart) != ccw(start, end, lineEnd)

    // xm_1 + c1 = xm2 + c2
    // x(m1 - m2) = c2 - c1
    // x = (c2-c1)/(m1-m2)
    fun intersectionPoint(lineStart: Point2D, lineEnd: Point2D): Point2D? {
        if (!intersects(lineStart, lineEnd)) return null
        val line2 = RealLine(lineStart, lineEnd)
        if (line2.m.isInfinite()) {
            return Point2D(line2.start.x, m * line2.start.x + c)
        } else if (m.isInfinite()) {
            return Point2D(start.x, line2.m * start.x + line2.c)
        }
        val x = (line2.c - c) / (m - line2.m)
        return Point2D(x, m * x + c)
    }

    // Radians
    fun copyWithAngle(angle: Double): RealLine {
        val newEnd = Point2D(cos(angle), sin(angle)) * magnitude + start
        return RealLine(start, newEnd)
    }

    fun normal(point: Point2D, flip: Boolean = false): RealLine {
        val normalM = if (m.isInfinite()) 0.0 else (-1 / m)
        if (normalM.isInfinite()) {
            return RealLine(point,
                point.add(0.0, 50.0 * (if (flip) -1.0 else 1.0)))
        }
        val add =
            Point2D(1.0, normalM).normalize() * 50.0 * (if (flip) -1.0 else 1.0)
        return RealLine(point, point + add)
    }
}

fun ccw(A: Point2D, B: Point2D, C: Point2D): Boolean {
    return (C.y - A.y) * (B.x - A.x) > (B.y - A.y) * (C.x - A.x)
}
