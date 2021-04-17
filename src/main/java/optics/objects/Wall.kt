package optics.objects

import javafx.Draggable
import javafx.KeyActions
import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import math.IntersectionSideData
import optics.InteractiveOpticalRectangle
import optics.TransformData
import optics.light.Ray
import java.util.function.Function

class Wall(
    x: Double,
    y: Double,
    width: Double,
    height: Double,
    val parent: Pane,
    rotation: Double
) : InteractiveOpticalRectangle(x, y, width, height) {
    private val onStateChange = mutableListOf<(Event)->Unit>()
    private var onDestroy: ((Event)->Unit)? = null
    private fun triggerStateChange(e: Event) {
        for (handler in onStateChange) {
            handler(e)
        }
    }

    private fun triggerDestroy(e: Event) {
        onDestroy?.invoke(e)
    }

    override fun transform(r: Ray, iPoint: Point2D): TransformData? {
        val l = r.currentJavaFXLine
        l.endX = iPoint.x
        l.endY = iPoint.y
        return null
    }

    override fun getIntersectionSideData(
        iPoint: Point2D,
        origin: Point2D,
        r: Ray
    ): IntersectionSideData? {
        return null
    }

    override fun drawNormal(
        iData: IntersectionSideData,
        iPoint: Point2D
    ): Line? {
        return null
    }

    override fun addOnStateChange(handler: (Event)->Unit) {
        onStateChange.add(handler)
    }

    override fun setOnDestroy(onDestroy: (Event)->Unit) {
        this.onDestroy = onDestroy
    }

    override fun cloneObject(): Wall {
        return this.clone(false)
    }

    override fun serialize(): String {
        return super.serialize('w')
    }

    override fun clone(shiftPositions: Boolean): Wall {
        return Wall(x + if (shiftPositions) 10 else 0,
            y + if (shiftPositions) 10 else 0,
            width,
            height,
            parent,
            this.rotate)
    }

    init {
        this.rotate = rotation
        fill = Color.rgb(180, 179, 176)
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
