package javafx

import application.Storage
import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Shape
import optics.InteractiveOpticalRectangle
import optics.light.RayCircle
import optics.objects.OpticalRectangle
import optics.objects.Refract
import utils.plus
import utils.toRadians

class KeyActions(
    private val shape: Shape,
    parent: Pane,
    onDestroy: () -> Unit,
) {
    init {
        shape.onMouseClicked =
            EventHandler { shape.requestFocus() }
        shape.onKeyPressed = EventHandler { event: KeyEvent ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            val eventCode = event.code.toString()
            if (eventCode == "DELETE") {
                parent.children.remove(shape)
                onDestroy()
            }
            if (event.isControlDown) {
                if (eventCode == "D") {
                    if (shape is InteractiveOpticalRectangle) {
                        val newRectangle = shape.clone(true)
                        Storage.opticsTabController!!.addObject(newRectangle as InteractiveOpticalRectangle,
                            shape.realParent!!)
                    } else if (shape is RayCircle) {
                        val newRay = shape.clone()
                        Storage.rayTabController!!.createRay(newRay)
                    }
                }
            }
            if (shape is Refract) {
                val obj = shape
                if (eventCode == "ADD" || event.isShiftDown && eventCode == "EQUALS") {
                    obj.refractiveIndex = obj.refractiveIndex + 0.01
                    Storage.reRenderAll()
                    return@EventHandler
                } else if (eventCode == "SUBTRACT" || eventCode == "MINUS") {
                    obj.refractiveIndex = obj.refractiveIndex - 0.01
                    Storage.reRenderAll()
                    return@EventHandler
                }
            }
            if (event.isShiftDown) {
                // Move object instead of rotating it
                when (eventCode) {
                    "LEFT" -> {
                        if (shape is RayCircle) {
                            val r = shape.ray
                            val newRealLine =
                                r.realLine.copy(start = r.realLine.start + Point2D(
                                    -1.0,
                                    0.0))
                            r.update(newRealLine)
                        } else if (shape is OpticalRectangle) {
                            shape.setScreenX(shape.x - 1)
                        }
                    }
                    "RIGHT" -> {
                        if (shape is RayCircle) {
                            val r = shape.ray
                            val newRealLine =
                                r.realLine.copy(start = r.realLine.start + Point2D(
                                    1.0,
                                    0.0))
                            r.update(newRealLine)
                        } else if (shape is OpticalRectangle) {
                            shape.setScreenX(shape.x + 1)
                        }
                    }
                    "UP" -> {
                        if (shape is RayCircle) {
                            val r = shape.ray
                            val newRealLine =
                                r.realLine.copy(start = r.realLine.start + Point2D(
                                    0.0,
                                    -1.0))
                            r.update(newRealLine)
                        } else if (shape is OpticalRectangle) {
                            shape.setScreenY(shape.y - 1)
                        }
                    }
                    "DOWN" -> {
                        if (shape is RayCircle) {
                            val r = shape.ray
                            val newRealLine =
                                r.realLine.copy(start = r.realLine.start + Point2D(
                                    0.0,
                                    1.0))
                            r.update(newRealLine)
                        } else if (shape is OpticalRectangle) {
                            shape.setScreenY(shape.y + 1)
                        }
                    }
                    else -> return@EventHandler
                }
                Storage.reRenderAll()
                return@EventHandler
            }
            if (event.isAltDown) {
                //        Move object instead of rotating it
                val optShape: OpticalRectangle =
                    if (shape is OpticalRectangle) {
                        shape
                    } else {
                        return@EventHandler
                    }
                when (eventCode) {
                    "LEFT" -> {
                        optShape.setWidthChecked(optShape.width - 1)
                    }
                    "RIGHT" -> {
                        optShape.setWidthChecked(optShape.width + 1)
                    }
                    "UP" -> {
                        optShape.setHeightChecked(optShape.height + 1)
                    }
                    "DOWN" -> {
                        optShape.setHeightChecked(optShape.height - 1)
                    }
                    else -> return@EventHandler
                }
                Storage.reRenderAll()

                return@EventHandler
            }
            val rotate = shape.rotate
            if (eventCode == "LEFT") {
                val deltaAngle = if (event.isControlDown) 45 else 1
                //        Rotate anticlockwise
                if (shape is RayCircle) {
                    val r = shape.ray
                    val newRealLine =
                        r.realLine.copyWithAngle(r.realLine.angle + deltaAngle.toDouble()
                            .toRadians())
                    r.update(newRealLine)
                    return@EventHandler
                }
                shape.rotate =
                    (rotate - deltaAngle) % 360
            } else if (eventCode == "RIGHT") {
                val deltaAngle = if (event.isControlDown) 45 else 1
                if (shape is RayCircle) {
                    val r = shape.ray
                    val newRealLine =
                        r.realLine.copyWithAngle(r.realLine.angle - deltaAngle.toDouble()
                            .toRadians())
                    r.update(newRealLine)
                    return@EventHandler
                }
                //        Clockwise
                shape.rotate =
                    (rotate + if (event.isControlDown) 45 else 1) % 360
            } else if (eventCode == "UP" && event.isControlDown) {
                shape.rotate = (360 - rotate) % 360
            } else if (eventCode == "DOWN" && event.isControlDown) {
                shape.rotate = (rotate - 180) % 360
            } else {
                return@EventHandler
            }
            Storage.reRenderAll()
        }
    }
}
