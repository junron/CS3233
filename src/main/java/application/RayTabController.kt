package application

import application.Storage.rays
import application.Storage.reRenderAll
import application.Storage.rerenderRay
import javafx.beans.value.ObservableValue
import javafx.event.Event
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ColorPicker
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import optics.PreciseJavaFXLine
import optics.light.Ray
import java.util.function.Function

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
            val l =
                PreciseJavaFXLine(parent.width / 2, parent.height / 2, parent
                    .width / 2 + 2500, parent.height / 2)
            l.preciseAngle = 0.0
            val r = Ray(l, parent)
            createRay(r)
        }
        rayRotation!!.textProperty()
            .addListener { o: ObservableValue<out String>?, ol: String?, strVal: String ->
                // Prevent changes when animating
                if (Storage.isAnimating) return@addListener
                if (strVal == expectedText) return@addListener
                if (strVal.isEmpty()) {
                    if (focusedRay == null) return@addListener
                    focusedRay!!.angle = 0.0
                    rerenderRay(focusedRay!!)
                    return@addListener
                }
                val value: Double = try {
                    strVal.toDouble()
                } catch (e: NumberFormatException) {
                    return@addListener
                }
                if (focusedRay == null) return@addListener
                focusedRay!!.angle = fixAngle(value).toDouble()
                rerenderRay(focusedRay!!)
            }
        rayColor!!.valueProperty()
            .addListener { o: ObservableValue<out Color>?, ol: Color?, color: Color ->
                // Prevent changes when animating
                if (Storage.isAnimating) return@addListener
                if (focusedRay == null) return@addListener
                if (color == expectedColor) return@addListener
                focusedRay!!.color = color
                changeFocus(focusedRay)
                reRenderAll()
            }
    }

    fun createRay(r: Ray) {
        changeFocus(r)
        rays.add(r)
        r.setOnDestroy { e: Event? ->
            rays.remove(r)
        }
        r.addOnStateChange { e: Event? ->
            changeFocus(r)
            rerenderRay(r)
        }
        r.setOnFocusStateChanged { state: Boolean ->
            if (state) changeFocus(r)
        }
        rerenderRay(r)
        expectedText = fixAngle(r.angle)
        focusedRay = r
        expectedColor = Color.BLACK
        rayRotation!!.text = expectedText
        rayColor!!.valueProperty().value = Color.BLACK
        r.requestFocus()
    }

    private fun changeFocus(r: Ray?) {
        if (focusedRay != null) {
            focusedRay!!.circle.stroke = Color.BLACK
            focusedRay!!.circle.strokeWidth = 1.0
        }
        focusedRay = r
        focusedRay!!.circle.stroke = Color.BLUE
        focusedRay!!.circle.strokeWidth = 2.0
        expectedText = fixAngle(r!!.angle)
        expectedColor = focusedRay!!.color
        rayColor!!.value = focusedRay!!.color
        rayRotation!!.text = expectedText
    }

    private fun fixAngle(angle: Double): String {
        var angle = angle
        angle %= 360.0
        if (angle < 0) angle += 360.0
        return angle.toString()
    }
}
