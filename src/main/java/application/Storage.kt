package application

import devices.Host
import javafx.scene.layout.Pane

object Storage {
    @JvmField
    val hosts = mutableListOf<Host>()

    @JvmField
    var devicesTabController: DevicesTabController? = null
    @JvmField
    var maximumReflectionDepth = 1000

    @JvmField
    var showLabels = true

    @JvmField
    var isAnimating = false
    
    @JvmField
    var parent: Pane? = null

    @JvmStatic
    fun clearAll() {
        // TODO: make device extend shape or something
        parent!!.children.removeAll(hosts.toSet())
        hosts.clear()
    }
    
    fun reRenderAll(){
        
    }
}
