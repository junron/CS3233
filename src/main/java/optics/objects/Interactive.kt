package optics.objects

import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.shape.Line
import math.IntersectionSideData
import optics.TransformData
import optics.light.Ray
import serialize.Serializable
import java.util.function.Function

interface Interactive<T : Interactive<T>> : Serializable {
    fun getIntersectionSideData(
        iPoint: Point2D,
        origin: Point2D,
        r: Ray
    ): IntersectionSideData?

    fun addOnStateChange(handler: (Event) -> Unit)
    fun setOnDestroy(onDestroy: (Event) -> Unit)
    fun cloneObject(): T
}
