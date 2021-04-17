package application

import application.Storage.opticalRectangles
import application.Storage.reRenderAll
import javafx.SettableTextField
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import optics.InteractiveOpticalRectangle
import optics.objects.Mirror
import optics.objects.OpticalRectangle
import optics.objects.Refract
import optics.objects.Wall
import utils.Geometry.fixAngle

class OpticsTabController {
    @FXML
    private var newWall: Button? = null

    @FXML
    private var newMirror: Button? = null

    @FXML
    private var newRefractor: Button? = null

    @FXML
    private var refractiveIndex: SettableTextField? = null

    @FXML
    private var width: SettableTextField? = null

    @FXML
    private var rotation: SettableTextField? = null

    @FXML
    private var height: SettableTextField? = null
    private var focusedObject: OpticalRectangle? = null
    fun initialize(parent: Pane) {
        newMirror!!.onMouseClicked = EventHandler { event: MouseEvent? ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            val m = Mirror(parent.width / 2,
                parent.height / 2 - 100,
                20.0,
                200.0,
                parent,
                0.0)
            addObject(m, parent)
        }
        newWall!!.onMouseClicked = EventHandler { event: MouseEvent? ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            val w = Wall(parent.width / 2,
                parent.height / 2 - 25,
                20.0,
                50.0,
                parent,
                0.0)
            addObject(w, parent)
        }
        newRefractor!!.onMouseClicked = EventHandler { event: MouseEvent? ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            val re = Refract(parent.width / 2,
                parent.height / 2 - 50,
                20.0,
                100.0,
                parent,
                0.0,
                1.0)
            addObject(re, parent)
        }
        rotation!!.textProperty()
            .addListener { o: ObservableValue<out String>?, ol: String?, `val`: String ->
                // Prevent changes when animating
                if (Storage.isAnimating) return@addListener
                if (focusedObject == null) return@addListener
                if (`val`.length == 0) {
                    focusedObject!!.rotate = 0.0
                    reRenderAll()
                    return@addListener
                }
                val value = validate(`val`, false) ?: return@addListener
                focusedObject!!.rotate = fixAngle(value).toDouble()
                reRenderAll()
            }
        width!!.setChangeListener { o: ObservableValue<out String>?, ol: String?, `val`: String ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@setChangeListener
            if (focusedObject == null) return@setChangeListener
            if (`val`.length == 0) {
                focusedObject!!.width = 5.0
                reRenderAll()
                return@setChangeListener
            }
            val value = validate(`val`, true) ?: return@setChangeListener
            focusedObject!!.setWidthChecked(value)
            reRenderAll()
        }
        height!!.textProperty()
            .addListener { o: ObservableValue<out String>?, ol: String?, `val`: String ->
                // Prevent changes when animating
                if (Storage.isAnimating) return@addListener
                if (focusedObject == null) return@addListener
                if (`val`.length == 0) {
                    focusedObject!!.height = 5.0
                    reRenderAll()
                    return@addListener
                }
                val value = validate(`val`, true) ?: return@addListener
                focusedObject!!.setHeightChecked(value)
                reRenderAll()
            }
        refractiveIndex!!.setChangeListener { o: ObservableValue<out String>?, ol: String?, value: String ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@setChangeListener
            if (focusedObject == null) return@setChangeListener
            if (focusedObject !is Refract) return@setChangeListener
            val obj = focusedObject as Refract
            if (value.isEmpty()) {
                obj.refractiveIndex = 1.0
                reRenderAll()
                return@setChangeListener
            }
            val value = validate(value, true) ?: return@setChangeListener
            if (value < 1) return@setChangeListener
            obj!!.refractiveIndex = value
            reRenderAll()
        }
    }

    fun addObject(obj: InteractiveOpticalRectangle, parent: Pane) {
        opticalRectangles.add(obj)
        obj.addOnStateChange {
            changeFocus(obj)
            reRenderAll()
        }
        obj.setOnDestroy {
            if (focusedObject === obj) focusedObject = null
            rotation!!.text = "-"
            height!!.text = "-"
            width!!.text = "-"
            refractiveIndex!!.text = "-"
            opticalRectangles.remove(obj)
            reRenderAll()
        }
        obj.focusedProperty()
            .addListener { o: ObservableValue<out Boolean>?, ol: Boolean?, state: Boolean ->
                if (state) changeFocus(obj)
            }
        reRenderAll()
        parent.children.add(obj)
        obj.requestFocus()
        changeFocus(obj)
    }

    private fun changeFocus(`object`: OpticalRectangle) {
        if (focusedObject != null) {
            focusedObject!!.stroke = Color.BLACK
            focusedObject!!.strokeWidth = 1.0
        }
        focusedObject = `object`
        focusedObject!!.stroke = Color.BLUE
        focusedObject!!.strokeWidth = 2.0
        rotation!!.text = fixAngle(`object`.rotate)
        width!!.text = `object`.width.toString()
        height!!.text = `object`.height.toString()
        if (`object` is Refract) refractiveIndex!!.text =
            `object`.refractiveIndex.toString()
    }

    private fun validate(value: String, positive: Boolean): Double? {
        val res: Double
        res = try {
            value.toDouble()
        } catch (e: NumberFormatException) {
            return null
        }
        return if (positive && res < 0) null else res
    }
}
