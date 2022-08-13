package devices

import javafx.scene.layout.Pane

open class Device(id: Int, x: Int, y: Int, parent: Pane, imagePath: String = "/host.png") :
    DraggableDevice(id, x, y, parent, imagePath) {
    
    override fun toString(): String {
        return super.toString().replace("DraggableDevice", "Device")
    }
}
