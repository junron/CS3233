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
        if (device !is Router) {
            return
        }
        if (device in connectedRouters) {
            connectedRouters.remove(device)
        }
    }


    companion object {
        fun deserialize(serialized: String, parent: Pane): Host {
            val parts = serialized.split("|")
            val id = parts[1].toInt()
            val x = parts[2].toDouble().toInt()
            val y = parts[3].toDouble().toInt()
            val ip = parts[4].toUIntOrNull()
            val host = Host(id, x, y, parent)
            host.ipAddress = ip
            return host
        }
    }
}
