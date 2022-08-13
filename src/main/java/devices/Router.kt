package devices

import application.FxAlerts
import application.Storage
import application.Storage.connectionLines
import javafx.scene.layout.Pane
import routing.Connection
import routing.ConnectionLine
import tornadofx.*
import utils.asIP
import utils.contains
import utils.div
import utils.isPrivateIP

class Router(id: Int, x: Int, y: Int, parent: Pane) : Device(id, x, y, parent, "/router.png") {

    override var ipAddress: UInt? = null
        set(value) {
            field = value
            if (value != null) {
                text.text = "${value.asIP()}/$cidrPrefix"
            }
        }
    var cidrPrefix: Int = 32
        set(value) {
            field = value
            if (ipAddress == null) {
                text.text = "No IP"
            } else {
                text.text = "${ipAddress?.asIP()}/$value"
            }
        }

//    private fun rejectChangeIfHasConnections(prop: String){
//        FxAlerts.error("Cannot change $prop", "Remove ").show()
//    }

    init {
        this.onDestroys += {
            parent.children.removeAll(this.thisConnectionLineMappings.values)
            connections.forEach {
                it.device2.deviceDeleted(this)
            }
        }
    }

    // Routers have connections to other hosts
    val connections = mutableListOf<Connection>()
    private val thisConnectionLineMappings = mutableMapOf<Int, ConnectionLine>()


    fun addConnection(other: Device) {
        if (other.id in thisConnectionLineMappings) {
            return
        }

        val thisIP = this.ipAddress
        val otherIP = other.ipAddress

        if (thisIP == null) {
            FxAlerts.error("Cannot create connection", "Router must have IP Address").show()
            return
        }


        // Hosts connecting must either have no IP or must belong in the subnet defined by the router
        if (other is Host) {
            if (otherIP != null && otherIP !in (thisIP / cidrPrefix)) {
                FxAlerts.error("Cannot create connection", "Host does not belong in this subnet").show()
                return
            }
        } else if (other is Router) {
            if (otherIP == null || thisIP.isPrivateIP()) {
                // Other will handle error
                return
            }
            // Routers cannot connect to other routers with private IPs
            if (otherIP.isPrivateIP()) {
                FxAlerts.error("Cannot create connection", "Cannot connect routers with private IPs").show()
                return
            }
        }

        // Handle DHCP
        if (Storage.autoDHCP && otherIP == null) {
            val usedIPAddresses = connections.filter { it.device2 is Host }.mapNotNull { it.device2.ipAddress }
            // Find min address or use router address + 1
            var candidateAddress = (usedIPAddresses.minOrNull() ?: thisIP) + 1U
            while (candidateAddress in usedIPAddresses && candidateAddress in thisIP / cidrPrefix) {
                candidateAddress += 1U
            }
            if (candidateAddress !in thisIP / cidrPrefix) {
                // Cannot allocate!
                FxAlerts.error("Cannot create connection", "DHCP failed!").show()
                return
            }
            other.ipAddress = candidateAddress
        }

        val connection = Connection(this, other)
        connections.add(connection)
        val connectionLine = ConnectionLine(connection)
        parent.add(connectionLine)
        connectionLines.add(connectionLine)
        thisConnectionLineMappings[other.id] = connectionLine
        connectionLine.toBack()

        // If other is host, add this router to their list of routers
        if (other is Host) {
            other.connectedRouters += this
        }

    }

    override fun deviceDeleted(device: Device) {
        super.deviceDeleted(device)
        connections.removeIf {
            it.device2 == device
        }
        if (device.id in thisConnectionLineMappings) {
            parent.children.remove(thisConnectionLineMappings[device.id])
            thisConnectionLineMappings.remove(device.id)
        }
    }

    override fun serialize(): String {
        // TODO
        return super.serialize() + ""
    }

    override fun toString(): String {
        return super.toString().replace("Device", "Router")
    }


}
