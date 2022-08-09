package application

import devices.DraggableDevice
import javafx.scene.layout.Pane
import routing.ConnectionLine

object Storage {
    val devices = mutableListOf<DraggableDevice>()
    
    val connectionLines = mutableListOf<ConnectionLine>()
    
    var generalTabController: GeneralTabController? = null

    @JvmField
    var isAnimating = false
    
    @JvmField
    var parent: Pane? = null

    @JvmStatic
    fun clearAll() {
        parent!!.children.removeAll(devices.toSet())
        devices.clear()
    }
    
    fun reRenderAll(){
        
    }
}
