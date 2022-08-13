package application

import application.Storage.clearAll
import application.Storage.devices
import application.Storage.reRenderAll
import devices.Device
import devices.DraggableDevice
import devices.Host
import devices.Router
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
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

    private var focusedObject: DraggableDevice? = null

    @FXML
    private var save: Button? = null

    @FXML
    private var load: Button? = null

    @FXML
    private var clearAll: Button? = null

    @FXML
    private var connectionMode: CheckBox? = null

    private fun connectionModeEnabled(): Boolean = connectionMode?.isSelected ?: false

    fun initialize(parent: Pane) {
        save!!.onMouseClicked = EventHandler { event: MouseEvent? ->
            val allObjects: MutableList<Serializable> = devices.toMutableList()
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


        connectionMode!!.onMouseClicked = EventHandler {
            Storage.connectionMode = connectionModeEnabled()
            unfocus()
        }


        newHost!!.onMouseClicked = EventHandler { _: MouseEvent? ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            val h = Host(
                Random.nextInt(), Math.floorDiv(parent.width.toInt(), 2),
                Math.floorDiv(parent.height.toInt(), 2),
                parent
            )

            h.onMouseClicked = EventHandler clickHandler@{ evt: MouseEvent ->
                val other = focusedObject
                focusedObject?.unfocus()
                h.focus()
                evt.consume()
                focusedObject = h
                // TODO: What to do when not in connection mode
                if (!connectionModeEnabled()) return@clickHandler
                if (other is Router) {
                    other.addConnection(h)
                }
            }

            addObject(h, parent)
        }
        newRouter!!.onMouseClicked = EventHandler { _: MouseEvent? ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            val r = Router(
                Random.nextInt(), Math.floorDiv(parent.width.toInt(), 2),
                Math.floorDiv(parent.height.toInt(), 2),
                parent
            )

            r.onMouseClicked = EventHandler clickHandler@{ evt: MouseEvent ->
                val other = focusedObject
                focusedObject?.unfocus()
                focusedObject = r
                r.focus()
                evt.consume()
                // TODO: What to do when not in connection mode
                if (!connectionModeEnabled()) return@clickHandler
                if (other != null && other != r) {
                    r.addConnection(other)
                    if (other is Router) {
                        other.addConnection(r)
                    }
                }
                println(r.connections)
            }
            addObject(r, parent)
        }

        parent.onMouseClicked = EventHandler {
            unfocus()
        }
    }

    private fun unfocus() {
        focusedObject?.unfocus()
        focusedObject = null
    }

    fun addObject(obj: Device, parent: Pane) {
        devices.add(obj)
        obj.onDestroys += {
            if (focusedObject == obj) focusedObject = null
            devices.remove(obj)
            reRenderAll()
        }
        reRenderAll()
        parent.children.add(obj)
        obj.requestFocus()
    }

}
