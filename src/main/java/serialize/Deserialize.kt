package serialize

import application.Storage.generalTabController
import devices.Device
import devices.Host
import devices.Router
import javafx.scene.layout.Pane

object Deserialize {
    fun deserialize(obj: String, parent: Pane): Device {
        return when (obj[0]) {
            'h' -> {
                val h = Host(0, 0, 0, parent)
                h.deserialize(obj)
                h
            }

            'r' -> {
                val router = Router(0, 0, 0, parent)
                router.deserialize(obj)
                router
            }

            else -> throw Exception("Device does not exist")
        }
    }

    fun deserializeAndAdd(obj: String, parent: Pane) {
        val serializable = deserialize(obj, parent)
        generalTabController?.addObject(serializable, parent)
    }
}
