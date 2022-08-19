package application

import devices.DraggableDevice
import javafx.scene.layout.Pane
import routing.ConnectionLine
import utils.Subnet

object Storage {
    val devices = mutableListOf<DraggableDevice>()

    val connectionLines = mutableListOf<ConnectionLine>()

    val subnets = mutableSetOf<Subnet>()

    var generalTabController: GeneralTabController? = null

    var connectionMode: Boolean = false
    
    var showIfaceIP: Boolean = false
        set(value) {
            field = value
            connectionLines.forEach {
                it.setLabelVisibility(value)
            }
        }

    @JvmField
    var isAnimating = false

    @JvmField
    var parent: Pane? = null

    @JvmStatic
    fun clearAll() {
        parent!!.children.removeAll(devices.toSet())
        parent!!.children.removeAll(connectionLines.toSet())
        devices.clear()
        connectionLines.clear()
    }

    fun resetConnectionLines() {
        connectionLines.forEach { it.unhighlight() }
    }
}
