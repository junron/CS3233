package optics.objects

import javafx.AngleDisplay
import javafx.Draggable
import javafx.KeyActions
import javafx.event.Event
import javafx.event.EventHandler
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
import optics.TransformData
import optics.light.Ray
import utils.Geometry
import java.util.function.Function

class Mirror(
    x: Double,
    y: Double,
    width: Double,
    height: Double,
    val parent: Pane,
    rotation: Double
) : InteractiveOpticalRectangle(x, y, width, height) {
    private val onStateChange = mutableListOf<((Event)->Unit)>()
    private var onDestroy: ((Event)->Unit)? = null
    override fun addOnStateChange(handler: (Event)->Unit) {
        onStateChange += handler
    }

    private fun triggerStateChange(e: Event) {
        for (handler in onStateChange) {
            handler(e)
        }
    }

    private fun triggerDestroy(e: Event) {
        onDestroy?.invoke(e)
    }

    override fun setOnDestroy(onDestroy: (Event)->Unit) {
        this.onDestroy = onDestroy
    }

    override fun cloneObject(): Mirror {
        return this.clone(false)
    }

    override fun transform(r: Ray, iPoint: Point2D): TransformData? {
        val l = r.currentJavaFXLine
        l.endX = iPoint.x
        l.endY = iPoint.y
        val iData =
            this.getIntersectionSideData(iPoint, Point2D(l.startX, l.startY), r) ?: return null
        val failedAngles = ArrayList<Double>()
        val normalAngle: Double
        var intersectionAngle = 0.0
        var preciseJavaFXLine: PreciseJavaFXLine? = null
        for (i in 0..4) {
            if (iData.normalVector == null) {
                println("ERRORORROOROR: iData is null")
                return null
            }
            normalAngle = iData.normalAngle
            intersectionAngle =
                Intersection.getObjectIntersectionAngle(iData, l)
            val newLine = Geometry.createLineFromPoints(iPoint, iPoint
                .add(Vectors.constructWithMagnitude(normalAngle - intersectionAngle,
                    250000.0)))
            preciseJavaFXLine = PreciseJavaFXLine(newLine)
            preciseJavaFXLine.preciseAngle = normalAngle - intersectionAngle

            // Ray is going through the mirror
            // Something is wrong, abort
            if (Intersection.hasExitPoint(intersect(preciseJavaFXLine, this),
                    iPoint)
            ) {
                println("Null")
                return null
                // failedAngles.add(iData.normalAngle);
                // iData = Intersection
                //         .getIntersectionSide(r, iPoint, this, new Point2D(l.getStartX(), l.getStartY()), false,
                //         failedAngles);
                // System.out.println("Cancelled");
                // if (i == 4) return null;
                // continue;
            }
            break
        }
        var angle = Math.toDegrees(intersectionAngle) % 360
        if (angle > 180) angle =
            360 - angle else if (angle < -180) angle += 360.0
        val angleDisplay = AngleDisplay("Incidence",
            String.format("%.1f", -angle),
            "Reflection",
            String.format("%.1f", angle))
        return TransformData(preciseJavaFXLine!!, angleDisplay, iData)
    }

    override fun getIntersectionSideData(
        iPoint: Point2D,
        origin: Point2D,
        r: Ray
    ): IntersectionSideData? {
        return Intersection.getIntersectionSide(r, iPoint, this, origin, false)
    }

    override fun drawNormal(
        iData: IntersectionSideData,
        iPoint: Point2D
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
        Draggable(this,
            { e: Event -> triggerStateChange(e) },
            { e: Event -> triggerDestroy(e) },
            parent)
        KeyActions(this,
            { e: KeyEvent -> triggerStateChange(e) },
            { e: Event -> triggerDestroy(e) },
            parent)
    }
}
