package serialize

import application.Storage
import devices.Device
import devices.Host
import devices.Router
import javafx.scene.layout.Pane

fun deserialize(deviceStrings: List<String>, parent: Pane): List<Device> {
    val devices = deviceStrings.map {
        when (it[0]) {
            'h' -> Host.deserialize(it, parent)
            'r' -> Router.deserialize(it, parent)
            else -> throw Error("Invalid device type")
        }
    }

    deviceStrings.forEach { deviceString ->
        if (deviceString[0] == 'r') {
            val parts = deviceString.split("|")
            val router = devices.find { it.id == parts[1].toInt() } as Router
            val connectedIDs = parts.subList(6, parts.size)
            connectedIDs.filter { it.isNotEmpty() }.forEach { id ->
                val device2 = devices.find { it.id == id.toInt() }!!
                router.addConnection(device2)
            }
        }
    }

    return devices
}
