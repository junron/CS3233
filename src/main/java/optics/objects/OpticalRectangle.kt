package optics.objects

import application.Storage
import javafx.geometry.Point2D
import javafx.scene.layout.Pane
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import math.IntersectionSideData
import optics.TransformData
import optics.light.Ray
import serialize.Serializable

abstract class OpticalRectangle(
    x: Double,
    y: Double,
    width: Double,
    height: Double
) : Rectangle(x, y, width, height), Serializable {
    private val maxSize = 1000
    private val minSize = 5
    private var realX: Double
    private var realY: Double
    var realParent: Pane? = null
        protected set

    fun setWidthChecked(width: Double) {
        if (width > maxSize) {
            this.width = maxSize.toDouble()
            return
        }
        if (width < minSize) {
            this.width = minSize.toDouble()
            return
        }
        this.width = width
    }

    fun setHeightChecked(height: Double) {
        if (height > maxSize) {
            this.height = maxSize.toDouble()
            return
        }
        if (height < minSize) {
            this.height = minSize.toDouble()
            return
        }
        this.height = height
    }

    abstract fun clone(shiftPositions: Boolean): OpticalRectangle
    abstract fun transform(r: Ray, iPoint: Point2D): TransformData?
    abstract fun drawNormal(iData: IntersectionSideData, iPoint: Point2D): Line?


    fun serialize(id: Char): String {
        return "$id|$realX|$realY|$width|$height|$rotate"
    }

    override fun deserialize(string: String) {
        val parts = string.split("\\|").toTypedArray()
        realX = parts[1].toDouble()
        realY = parts[2].toDouble()
        reposition()
        width = parts[3].toDouble()
        height = parts[4].toDouble()
        this.rotate = parts[5].toDouble()
    }

    open fun clone(opticalRectangle: OpticalRectangle) {
        opticalRectangle.x = x
        opticalRectangle.y = y
        opticalRectangle.realY = realY
        opticalRectangle.realX = realX
        opticalRectangle.rotate = this.rotate
        opticalRectangle.height = height
        opticalRectangle.width = width
    }

    fun setScreenX(x: Double) {
        this.x = x
        realX = x - Storage.offset.x
    }

    fun setScreenY(y: Double) {
        this.y = y
        realY = y - Storage.offset.y
    }

    fun reposition() {
        x = realX + Storage.offset.x
        y = realY + Storage.offset.y
    }

    init {
        realX = x - Storage.offset.x
        realY = y - Storage.offset.y
        this.viewOrder = 100.0
    }
}
