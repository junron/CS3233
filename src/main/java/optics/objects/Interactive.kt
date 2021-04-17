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

interface Interactive : Serializable {
    fun transform(r: Ray, iPoint: Point2D): TransformData
    fun getIntersectionSideData(
        iPoint: Point2D,
        origin: Point2D,
        r: Ray?
    ): IntersectionSideData

    fun drawNormal(iData: IntersectionSideData, iPoint: Point2D): Line
    fun addOnStateChange(handler: EventHandler<Event>)
    fun setOnDestroy(onDestroy: Function<Event, Void>)
    fun cloneObject(): Interactive
}
