package devices

import application.Storage.connectionLines
import javafx.scene.layout.Pane
import routing.Connection
import routing.ConnectionLine
import tornadofx.*

class Router(id: Int, x: Int, y: Int, parent: Pane) : Device(id, x, y, parent, "/router.png") {

    init {
        this.text.text = "Amongus"

        this.onDestroys += {
            parent.children.removeAll(this.thisConnectionLineMappings.values)
            connections.forEach {
                it.device2.deviceDeleted(this)
            }
        }
    }

    // Routers have connections to other hosts
    val connections = mutableListOf<Connection>()
    private val thisConnectionLineMappings = mutableMapOf<Int, ConnectionLine>()


    fun addConnection(other: DraggableDevice) {
        if (other.id in thisConnectionLineMappings) {
            return
        }
        val connection = Connection(this, other)
        connections.add(connection)
        val connectionLine = ConnectionLine(connection)
        parent.add(connectionLine)
        connectionLines.add(connectionLine)
        thisConnectionLineMappings[other.id] = connectionLine
        connectionLine.toBack()

        // If other is host, add this router to their list of routers
        if (other is Host) {
            other.connectedRouters += this
        }
    }

    override fun deviceDeleted(device: DraggableDevice) {
        super.deviceDeleted(device)
        connections.removeIf {
            it.device2 == device
        }
        if (device.id in thisConnectionLineMappings) {
            parent.children.remove(thisConnectionLineMappings[device.id])
            thisConnectionLineMappings.remove(device.id)
        }
    }

    override fun serialize(): String {
        // TODO
        return super.serialize() + ""
    }

    override fun toString(): String {
        return super.toString().replace("Host", "Router")
    }


}
