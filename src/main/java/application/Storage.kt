package application

import devices.DraggableDevice
import javafx.scene.layout.Pane
import routing.ConnectionLine

object Storage {
    val devices = mutableListOf<DraggableDevice>()
    
    val connectionLines = mutableListOf<ConnectionLine>()
    
    var generalTabController: GeneralTabController? = null
    
    var connectionMode: Boolean = false
    
    var autoDHCP: Boolean = false

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
    
    fun reRenderAll(){
        
    }
}
