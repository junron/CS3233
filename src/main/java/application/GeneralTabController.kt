package application

import application.Storage.clearAll
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
import javafx.stage.Stage
import serialize.Deserialize
import serialize.FileOps
import serialize.Serializable
import java.io.IOException
import kotlin.random.Random

class GeneralTabController {
    @FXML
    private var newRouter: Button? = null

    @FXML
    private var newHost: Button? = null
    
    private var focusedObject: Host? = null
    
    @FXML
    private var save: Button? = null

    @FXML
    private var load: Button? = null

    @FXML
    private var clearAll: Button? = null

    fun initialize(parent: Pane) {
        save!!.onMouseClicked = EventHandler { event: MouseEvent? ->
            val allObjects: MutableList<Serializable> = hosts.toMutableList()
            try {
                FileOps.save(allObjects, parent.scene.window as Stage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        load!!.onMouseClicked = EventHandler { e: MouseEvent? ->
            val data: ArrayList<String> = try {
                FileOps.load(parent.scene.window as Stage)
            } catch (ex: IOException) {
                ex.printStackTrace()
                return@EventHandler
            } ?: return@EventHandler
            for (obj in data) {
                Deserialize.deserializeAndAdd(obj, parent)
            }
        }
        clearAll!!.onMouseClicked =
            EventHandler { event: MouseEvent? -> clearAll() }
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
}
