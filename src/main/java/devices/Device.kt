package devices

import javafx.scene.layout.Pane
import utils.IPV4
import utils.Subnet

abstract class Device(id: Int, x: Int, y: Int, parent: Pane, imagePath: String = "/host.png") :
    DraggableDevice(id, x, y, parent, imagePath) {

    open var ipAddress: IPV4? = null
        set(value) {
            field = value
            if (value != null) {
                text.text = value.toString()
            }
        }

    override fun serialize(): String {
        return "h|$id|${this.layoutX}|${this.layoutY}|${ipAddress?.uintIp}"
    }

    open fun deviceDeleted(device: Device) {
        println("This is $id, removed ${device.id}")
    }

    abstract fun routeTo(target: IPV4, visited: List<Device>): List<Device>?
    
    abstract val subnet: Subnet?

    override fun toString(): String {
        return "Device(id=$id, ip=$ipAddress)"
    }
}
