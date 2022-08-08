package application

import application.Storage.clearAll
import application.Storage.maximumReflectionDepth
import application.Storage.hosts
import application.Storage.reRenderAll
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.stage.Stage
import serialize.Deserialize
import serialize.FileOps
import serialize.Serializable
import java.io.IOException

class GeneralTabController {

    @FXML
    private var showAngles: CheckBox? = null

    @FXML
    private var save: Button? = null

    @FXML
    private var load: Button? = null

    @FXML
    private var clearAll: Button? = null

    @FXML
    private var maxInteract: TextField? = null


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
        val maxInteract = maxInteract ?: return
        maxInteract.onKeyPressed = EventHandler {
            val maxInts: Int
            try {
                maxInts = maxInteract.text.toInt()
                if (maxInts < 10) throw NumberFormatException()
            } catch (e: NumberFormatException) {
                maxInteract.text = maximumReflectionDepth.toString()
                return@EventHandler
            }
            maximumReflectionDepth = maxInts
            reRenderAll()
        }
    }

    @FXML
    private fun triggerShowAnglesChange() {
        Storage.showLabels = showAngles!!.isSelected
        reRenderAll()
    }
}
