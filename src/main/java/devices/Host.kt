package devices

import javafx.scene.layout.Pane
import utils.IPV4
import utils.Subnet

class Host(id: Int, x: Int, y: Int, parent: Pane) : Device(id, x, y, parent) {

    var connectedRouter: Router? = null

    init {
        this.onDestroys += {
            // Inform connected routers that I'm dead
            connectedRouter?.deviceDeleted(this)
        }
    }

    override fun deviceDeleted(device: Device) {
        super.deviceDeleted(device)
        if (device !is Router) {
            return
        }
        connectedRouter = null
    }

    override fun routeTo(target: IPV4, visited: List<Device>): List<Device>? {
        if (target == ipAddress) {
            return listOf(this)
        }
        return connectedRouter?.routeTo(target, visited)
    }

    override val subnet: Subnet?
        get() = connectedRouter?.subnet


    companion object {
        fun deserialize(serialized: String, parent: Pane): Host {
            val parts = serialized.split("|")
            val id = parts[1].toInt()
            val x = parts[2].toDouble().toInt()
            val y = parts[3].toDouble().toInt()
            val ip = parts[4].toUIntOrNull()
            val host = Host(id, x, y, parent)
            host.ipAddress = ip?.let { IPV4(it) }
            return host
        }
    }
}
