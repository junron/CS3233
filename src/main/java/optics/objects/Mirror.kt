package optics.objects

import javafx.AngleDisplay
import javafx.Draggable
import javafx.KeyActions
import javafx.event.Event
import javafx.geometry.Point2D
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import math.Intersection
import math.IntersectionSideData
import math.Vectors
import optics.InteractiveOpticalRectangle
import optics.PreciseJavaFXLine
import optics.RealLine
import optics.TransformData
import optics.light.Ray
import utils.Geometry
import utils.toDegrees
import kotlin.math.abs

class Mirror(
    x: Double,
    y: Double,
    width: Double,
    height: Double,
    val parent: Pane,
    rotation: Double,
) : InteractiveOpticalRectangle(x, y, width, height, rotation) {
    private var onDestroy: (() -> Unit)? = null

    private fun triggerDestroy() {
        onDestroy?.invoke()
    }

    override fun setOnDestroy(onDestroy: () -> Unit) {
        this.onDestroy = onDestroy
    }

    override fun cloneObject(): Mirror {
        return this.clone(false)
    }

    override fun transform(r: Ray, iPoint: Point2D): TransformData? {
        val l = r.realLine
        val intersectLine = boundingLines().first { it.passesThrough(iPoint) }
        val n1 = intersectLine.normal(iPoint)
        val n2 = intersectLine.normal(iPoint, true)
        val normal =
            if (n1.end.distance(l.start) < n2.end.distance(l.start)) {
                n1
            } else n2
        println(abs(normal.angle - l.angle).toDegrees())
        val l1 = normal.copyWithAngle(normal.angle + abs(normal.angle - l.angle))
        val l2 = normal.copyWithAngle(normal.angle - abs(normal.angle - l.angle))
        val ray = if (l1.end.distance(l.start) < l2.end.distance(l.start)) {
            l1
        } else l2
        return TransformData(ray)
//        val failedAngles = ArrayList<Double>()
//        val normalAngle: Double
//        var intersectionAngle = 0.0
//        var preciseJavaFXLine: PreciseJavaFXLine? = null
//        for (i in 0..4) {
//            if (iData.normalVector == null) {
//                println("ERRORORROOROR: iData is null")
//                return null
//            }
//            normalAngle = iData.normalAngle
//            intersectionAngle =
//                Intersection.getObjectIntersectionAngle(iData, l)
//            val newLine = Geometry.createLineFromPoints(iPoint, iPoint
//                .add(Vectors.constructWithMagnitude(normalAngle - intersectionAngle,
//                    250000.0)))
//            preciseJavaFXLine = PreciseJavaFXLine(newLine)
//            preciseJavaFXLine.preciseAngle = normalAngle - intersectionAngle
//
//            // Ray is going through the mirror
//            // Something is wrong, abort
//            if (Intersection.hasExitPoint(intersect(preciseJavaFXLine, this),
//                    iPoint)
//            ) {
//                println("Null")
//                return null
//                // failedAngles.add(iData.normalAngle);
//                // iData = Intersection
//                //         .getIntersectionSide(r, iPoint, this, new Point2D(l.getStartX(), l.getStartY()), false,
//                //         failedAngles);
//                // System.out.println("Cancelled");
//                // if (i == 4) return null;
//                // continue;
//            }
//            break
//        }
//        var angle = Math.toDegrees(intersectionAngle) % 360
//        if (angle > 180) angle =
//            360 - angle else if (angle < -180) angle += 360.0
//        val angleDisplay = AngleDisplay("Incidence",
//            String.format("%.1f", -angle),
//            "Reflection",
//            String.format("%.1f", angle))
//        return TransformData(preciseJavaFXLine!!, angleDisplay, iData)
    }

    override fun getIntersectionSideData(
        iPoint: Point2D,
        origin: Point2D,
        r: Ray,
    ): IntersectionSideData? {
        return Intersection.getIntersectionSide(r, iPoint, this, origin, false)
    }

    override fun drawNormal(
        iData: IntersectionSideData,
        iPoint: Point2D,
    ): Line {
        val normalLength = 50.0
        val l = Geometry.createLineFromPoints(iPoint,
            iPoint.add(iData.normalVector.multiply(-normalLength / 2)))
        l.strokeDashArray.addAll(4.0)
        return l
    }

    override fun serialize(): String {
        return super.serialize('m')
    }

    override fun clone(shiftPositions: Boolean): Mirror {
        return Mirror(x + if (shiftPositions) 10 else 0,
            y + if (shiftPositions) 10 else 0,
            width,
            height,
            parent,
            this.rotate)
    }

    init {
        this.rotate = rotation
        arcHeight = 0.0
        arcWidth = 0.0
        fill = Color.color(5 / 255.0, 213 / 255.0, 255 / 255.0, 0.28)
        this.strokeWidth = 1.0
        stroke = Color.BLACK
        realParent = parent
        Draggable(this, parent) {
            triggerDestroy()
        }
        KeyActions(this, parent) {
            triggerDestroy()
        }
    }
}
