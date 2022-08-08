package devices

import javafx.scene.layout.Pane

class Router(id: Int, x: Int, y: Int, parent: Pane) : Host(id, x, y, parent){
    
    // Routers have connections to other hosts

    override fun serialize(): String {
        // TODO
        return super.serialize() + ""
    }

}
