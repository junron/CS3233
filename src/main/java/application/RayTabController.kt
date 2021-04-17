package application

import application.Storage.rays
import application.Storage.reRenderAll
import application.Storage.rerenderRay
import javafx.beans.value.ObservableValue
import javafx.event.Event
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Point2D
import javafx.scene.control.Button
import javafx.scene.control.ColorPicker
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import optics.RealLine
import optics.light.Ray
import utils.toDegrees

class RayTabController {
    @FXML
    private var newRay: Button? = null

    @FXML
    private var rayRotation: TextField? = null

    @FXML
    private var rayColor: ColorPicker? = null
    var focusedRay: Ray? = null
        private set
    private var expectedText: String? = null
    private var expectedColor: Color? = null
    fun initialize(parent: Pane) {
        newRay!!.onMouseClicked = EventHandler { e: MouseEvent? ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            val l = RealLine(Point2D(parent.width / 2, parent.height / 2),
                Point2D(parent.width / 2 + 25000, parent.height / 2))
            val r = Ray(l, Color.BLACK, parent)
            createRay(r)
        }
        rayRotation!!.textProperty()
            .addListener { o: ObservableValue<out String>?, ol: String?, strVal: String ->
                // Prevent changes when animating
                if (Storage.isAnimating) return@addListener
                if (strVal == expectedText) return@addListener
                val ray = focusedRay ?: return@addListener
                if (strVal.isEmpty()) {
                    val newRealLine = ray.realLine.copyWithAngle(0.0)
                    ray.update(newRealLine)
                    return@addListener
                }
                val value: Double = try {
                    strVal.toDouble()
                } catch (e: NumberFormatException) {
                    return@addListener
                }
                val newRealLine = ray.realLine.copyWithAngle(value)
                ray.update(newRealLine)
            }
        rayColor!!.valueProperty()
            .addListener { _: ObservableValue<out Color>?, _: Color?, color: Color ->
                // Prevent changes when animating
                if (Storage.isAnimating) return@addListener
                val ray = focusedRay ?: return@addListener
                if (color == expectedColor) return@addListener
                ray.update(newColor = color)
            }
    }

    fun createRay(r: Ray) {
        changeFocus(r)
        rays.add(r)
        r.setOnDestroy {
            rays.remove(r)
        }
        r.addOnStateChange {
            changeFocus(it)
            rerenderRay(it)
        }
        r.setOnFocusStateChanged { state: Boolean ->
            if (state) changeFocus(r)
        }
        expectedText = fixAngle(r.realLine.angle.toDegrees())
        focusedRay = r
        expectedColor = Color.BLACK
        rayRotation!!.text = expectedText
        rayColor!!.valueProperty().value = Color.BLACK
        rerenderRay(r)
        r.requestFocus()
    }

    private fun changeFocus(r: Ray) {
        if (focusedRay != null) {
            focusedRay!!.circle.stroke = Color.BLACK
            focusedRay!!.circle.strokeWidth = 1.0
        }
        focusedRay = r
        r.circle.stroke = Color.BLUE
        r.circle.strokeWidth = 2.0
        expectedText = fixAngle(r.realLine.angle.toDegrees())
        expectedColor = r.color
        rayColor!!.value = r.color
        rayRotation!!.text = expectedText
    }

    private fun fixAngle(angle: Double): String {
        var angle = angle
        angle %= 360.0
        if (angle < 0) angle += 360.0
        return angle.toString()
    }
}
