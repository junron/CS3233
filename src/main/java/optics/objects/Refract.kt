package optics.objects

import javafx.Draggable
import javafx.KeyActions
import javafx.geometry.Point2D
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import math.Intersection
import math.IntersectionSideData
import optics.InteractiveOpticalRectangle
import optics.TransformData
import optics.light.Ray
import utils.Geometry.createLineFromPoints
import kotlin.math.roundToInt

class Refract(
    x: Double,
    y: Double,
    width: Double,
    height: Double,
    val parent: Pane,
    rotation: Double,
    refractiveIndex: Double,
) : InteractiveOpticalRectangle(x, y, width, height) {
    private var onDestroy: (() -> Unit)? = null
    var refractiveIndex: Double = refractiveIndex
        set(value) {
            field = (value.coerceAtLeast(1.0) * 100).roundToInt() / 100.0
        }

    private fun triggerDestroy() {
        onDestroy?.invoke()
    }

    override fun setOnDestroy(onDestroy: () -> Unit) {
        this.onDestroy = onDestroy
    }


    override fun cloneObject(): Refract {
        return this.clone(false)
    }

    override fun transform(r: Ray, iPoint: Point2D): TransformData? {
        return null
//        val l = r.currentJavaFXLine
//        //    System.out.println(Math.toDegrees(l.getPreciseAngle()));
//        l.endX = iPoint.x
//        l.endY = iPoint.y
//        val iData =
//            getIntersectionSideData(iPoint, Point2D(l.startX, l.startY), r)
//        val intersectionAngle = Math.PI * 2 - l.preciseAngle
//        val normalAngle = iData.normalAngle
//        val incidence = Math.PI - intersectionAngle - normalAngle
//        var refAngle = Math.asin(Math.sin(incidence) / refractiveIndex)
//        if (r.isInRefractiveMaterial) {
//            r.isInRefractiveMaterial = false
//            refAngle = Math.asin(refractiveIndex * Math.sin(incidence))
//            //      Total internal reflection can only occur when light exits an object
//            if (java.lang.Double.isNaN(refAngle)) {
//                return totalInternalReflection(iPoint, r, iData)
//            }
//        } else {
//            r.isInRefractiveMaterial = true
//        }
//        val vect =
//            Vectors.constructWithMagnitude(refAngle + normalAngle - Math.PI,
//                250000.0)
//        val pLine =
//            PreciseJavaFXLine(createLineFromPoints(iPoint, iPoint.add(vect)))
//        pLine.preciseAngle = refAngle + normalAngle - Math.PI
//        var iAngle = Math.toDegrees(incidence) % 360
//        if (iAngle > 180) iAngle =
//            360 - iAngle else if (iAngle < -180) iAngle += 360.0
//        val angle = String.format("%.1f", Math.toDegrees(refAngle))
//        val iAngleStr = String.format("%.1f", iAngle)
//        val angleDisplay =
//            AngleDisplay("Incidence", iAngleStr, "Refraction", angle)
//        return TransformData(pLine, angleDisplay, iData)
    }

    //  Total internal reflection occurs when a ray travels from inside a high refractive index
    //  object to the air. The ray is internally reflected within the object
//    private fun totalInternalReflection(
//        iPoint: Point2D,
//        r: Ray,
//        iData: IntersectionSideData,
//    ): TransformData {
//        val normalAngle = iData.normalAngle
//        val intersectionAngle =
//            Intersection.getObjectIntersectionAngle(iData, r.currentJavaFXLine)
//        val pLine = PreciseJavaFXLine(createLineFromPoints(iPoint, iPoint
//            .add(Vectors.constructWithMagnitude(normalAngle - intersectionAngle,
//                250000.0))))
//        pLine.preciseAngle = normalAngle - intersectionAngle
//        r.isInRefractiveMaterial = true
//        //    Angle display
//        val angleDisplay = AngleDisplay("TIR",
//            String.format("%.1f",
//                Math.toDegrees(normalAngle - intersectionAngle)))
//        return TransformData(pLine, angleDisplay, iData)
//    }

    override fun getIntersectionSideData(
        iPoint: Point2D,
        origin: Point2D,
        r: Ray,
    ): IntersectionSideData {
        return Intersection.getIntersectionSide(r,
            iPoint,
            this,
            origin,
            r.isInRefractiveMaterial)
    }

    override fun drawNormal(
        iData: IntersectionSideData,
        iPoint: Point2D,
    ): Line {
        val normalLength = 50.0
        val l = createLineFromPoints(iPoint,
            iPoint.add(iData.normalVector.multiply(-normalLength / 2)))
        l.strokeDashArray.addAll(4.0)
        return l
    }

    override fun serialize(): String {
        //    x,y,width,height,rotation,ref index
        return super.serialize('e') + "|" + refractiveIndex
    }

    override fun deserialize(string: String) {
        super.deserialize(string)
        refractiveIndex = string.split("\\|").toTypedArray()[6].toDouble()
    }


    override fun clone(shiftPositions: Boolean): Refract {
        return Refract(x + if (shiftPositions) 10 else 0,
            y + if (shiftPositions) 10 else 0,
            width,
            height,
            parent,
            this.rotate,
            refractiveIndex)
    }

    override fun clone(opticalRectangle: OpticalRectangle) {
        super.clone(opticalRectangle)
        if (opticalRectangle is Refract) {
            opticalRectangle.refractiveIndex = refractiveIndex
        }
    }

    init {
        this.rotate = rotation
        arcHeight = 0.0
        arcWidth = 0.0
        fill = Color.color(5 / 255.0, 213 / 255.0, 255 / 255.0, 0.50)
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
