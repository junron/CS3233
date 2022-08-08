package javafx

import application.Storage.reRenderAll
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import math.Intersection

class Draggable(
    private val shape: Shape,
    private val parent: Pane,
    onDestroy: () -> Unit,
) {
    private var movementDelta: Point2D
    private fun isInUIArea(event: MouseEvent): Boolean {
        if (shape is Circle) {
            return event.sceneY >= parent.height - 165
        } else if (shape is Rectangle) {
            if (shape.y + shape.width + shape.height < parent
                    .height - 165
            ) {
                return false
            }
        }
        val intersection = (Shape.intersect(shape, shape) as Path).elements
        val points = Intersection.convertToPoints(intersection)
        return points.stream()
            .anyMatch { point2D: Point2D -> point2D.y >= parent.height - 165 }
    }

    init {
        movementDelta = Point2D.ZERO
//        if (shape is Rectangle) {
//            shape.setOnMousePressed { event: MouseEvent ->
//                movementDelta = Point2D(
//                    shape.x - event.sceneX, shape
//                        .y - event.sceneY)
//                event.consume()
//            }
//        } else if (shape is RayCircle) {
//            shape.setOnMousePressed { event: MouseEvent ->
//                movementDelta =
//                    Point2D((shape as Circle).centerX - event.sceneX,
//                        (shape as Circle)
//                            .centerY - event.sceneY)
//
//                event.consume()
//            }
//        }
        shape.onMouseDragged = EventHandler { event: MouseEvent ->
//            // Prevent changes when animating
//            if (Storage.isAnimating) return@EventHandler
//            // Prevent object from entering UI area
//            var prevY = 0.0
//            if (shape is OpticalRectangle) {
//                prevY = shape.y
//                shape.setScreenX(event.sceneX + movementDelta.x)
//                shape.setScreenY(event.sceneY + movementDelta.y)
//            } else if (shape is RayCircle) {
//                prevY = shape.centerY
//                val r = shape.ray
//                val newRealLine =
//                    r.realLine.copy(start = r.realLine.start - movementDelta,
//                        end = r.realLine.end - movementDelta)
//                r.update(newRealLine)
//                movementDelta =
//                    Point2D(shape.centerX - event.sceneX,
//                        shape.centerY - event.sceneY)
//                event.consume()
//                return@EventHandler
//            }
//            if (isInUIArea(event) &&  //        Except when moving object to trash
//                event.sceneX <= parent.width - 82
//            ) {
//                if (shape is OpticalRectangle) shape.setScreenY(prevY)
////                else if (shape is RayCircle) shape.ray.setScreenY(
////                    prevY)
//            }
//            event.consume()hen animating
//            if (Storage.isAnimating) return@EventHandler
//            // Prevent object from entering UI area
//            var prevY = 0.0
//            if (shape is OpticalRectangle) {
//                prevY = shape.y
//                shape.setScreenX(event.sceneX + movementDelta.x)
//                shape.setScreenY(event.sceneY + movementDelta.y)
//            } else if (shape is RayCircle) {
//                prevY = shape.centerY
//                val r = shape.ray
//                val newRealLine =
//                    r.realLine.copy(start = r.realLine.start - movementDelta,
//                        end = r.realLine.end - movementDelta)
//                r.update(newRealLine)
//                movementDelta =
//                    Point2D(shape.centerX - event.sceneX,
//                        shape.centerY - event.sceneY)
//                event.consume()
//                return@EventHandler
//            }
//            if (isInUIArea(event) &&  //        Except when moving object to trash
//                event.sceneX <= parent.width - 82
//            ) {
//                if (shape is OpticalRectangle) shape.setScreenY(prevY)
////                else if (shape is RayCircle) shape.ray.setScreenY(
////                    prevY)
//            }
//            event.consume()
            reRenderAll()
        }
        shape.onMouseReleased = EventHandler { e: MouseEvent ->
            if (e.sceneY > parent.height - 165 && e.sceneX > parent.width - 82) {
                parent.children.remove(shape)
                onDestroy()
            }
        }
    }
}
