package devices

import application.Storage.connectionLines
import javafx.scene.layout.Pane
import routing.Connection
import routing.ConnectionLine
import tornadofx.*

class Router(id: Int, x: Int, y: Int, parent: Pane) : Host(id, x, y, parent, "/router.png"){
    
    // Routers have connections to other hosts
    val connections = mutableListOf<Connection>()
    
    
    fun addConnection(other: DraggableDevice){
        val connection = Connection(this, other)
        connections.add(connection)
        val connectionLine = ConnectionLine(connection)
        parent.add(connectionLine)
        connectionLines.add(connectionLine)
        connectionLine.toBack()
    }

    override fun serialize(): String {
        // TODO
        return super.serialize() + ""
    }

    override fun toString(): String {
        return super.toString().replace("Host", "Router")
    }


}
