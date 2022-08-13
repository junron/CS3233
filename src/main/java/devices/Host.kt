package devices

import javafx.scene.layout.Pane

open class Host(id: Int, x: Int, y: Int, parent: Pane) : Device(id, x, y, parent) {

    val connectedRouters = mutableListOf<Router>()
    
    init {
        this.onDestroys += {
            // Inform connected routers that I'm dead
            connectedRouters.forEach { 
                it.deviceDeleted(this)
            }
        }
    }
    override fun deviceDeleted(device: Device) {
        super.deviceDeleted(device)
        if(device !is Router){
            return
        }
        if(device in connectedRouters){
            connectedRouters.remove(device)
        }
    }

  
}
