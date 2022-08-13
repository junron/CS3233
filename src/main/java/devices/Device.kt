package devices

import javafx.scene.layout.Pane
import utils.asIP

open class Device(id: Int, x: Int, y: Int, parent: Pane, imagePath: String = "/host.png") :
    DraggableDevice(id, x, y, parent, imagePath) {

    open var ipAddress: UInt? = null
        set(value) {
            field = value
            if (value != null) {
                text.text = value.asIP()
            }
        }

    override fun serialize(): String {
        return "d|$id|${this.layoutX}|${this.layoutY}|$ipAddress"
    }

    open fun deviceDeleted(device: Device) {
        println("This is $id, removed ${device.id}")
    }

    override fun toString(): String {
        return super.toString().replace("DraggableDevice", "Device")
    }
}
