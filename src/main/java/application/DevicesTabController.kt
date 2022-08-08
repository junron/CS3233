package application

import application.Storage.hosts
import application.Storage.reRenderAll
import devices.Host
import devices.Router
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import kotlin.random.Random

class DevicesTabController {
    @FXML
    private var newRouter: Button? = null

    @FXML
    private var newHost: Button? = null


    private var focusedObject: Host? = null
    fun initialize(parent: Pane) {
        newHost!!.onMouseClicked = EventHandler { event: MouseEvent? ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            val h = Host(
                Random.nextInt(), Math.floorDiv(parent.width.toInt(), 2),
                Math.floorDiv(parent.height.toInt(), 2),
                parent
            )
            addObject(h, parent)
        }
        newRouter!!.onMouseClicked = EventHandler { event: MouseEvent? ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            val r = Router(
                Random.nextInt(), Math.floorDiv(parent.width.toInt(), 2),
                Math.floorDiv(parent.height.toInt(), 2),
                parent
            )
            addObject(r, parent)
        }
    }

    fun addObject(obj: Host, parent: Pane) {
        hosts.add(obj)
        obj.onDestroy = {
            if (focusedObject == obj) focusedObject = null
            hosts.remove(obj)
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

    private fun changeFocus(device: Host) {
        focusedObject = device
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
