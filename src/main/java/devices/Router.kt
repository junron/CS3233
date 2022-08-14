package devices

import application.FxAlerts
import application.Storage
import application.Storage.connectionLines
import javafx.scene.layout.Pane
import routing.Connection
import routing.ConnectionLine
import tornadofx.*
import utils.*

class Router(id: Int, x: Int, y: Int, parent: Pane) : Device(id, x, y, parent, "/router.png") {

    override var ipAddress: IPV4? = null
        set(value) {
            val oldSubnet = subnet
            field = value
            if (value != null) {
                text.text = "$value/$cidrPrefix"
                if (oldSubnet != null) {
                    Storage.subnets -= oldSubnet
                }
                Storage.subnets += subnetNotNull
            }
        }

    var cidrPrefix: Int = 32
        set(value) {
            val oldSubnet = ipAddress?.let { subnet }
            field = value
            if (ipAddress == null) {
                text.text = "No IP"
            } else {
                text.text = "${ipAddress}/$value"
                if (oldSubnet != null) {
                    Storage.subnets -= oldSubnet
                }
                Storage.subnets += subnetNotNull
            }
        }

    override val subnet: Subnet?
        get() = ipAddress?.let { it / cidrPrefix }

    val subnetNotNull: Subnet
        get() = subnet!!

    init {
        this.onDestroys += {
            parent.children.removeAll(this.thisConnectionLineMappings.values)
            connections.forEach {
                it.device2.deviceDeleted(this)
            }
            if (subnet != null) {
                Storage.subnets -= subnet!!
            }
        }
    }

    // Routers have connections to other hosts
    val connections = mutableListOf<Connection>()
    private val thisConnectionLineMappings = mutableMapOf<Int, ConnectionLine>()


    fun addConnection(other: Device) {
        println("$this is connecting to $other")
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
            val usedIPAddresses: List<UInt> =
                connections.filter { it.device2 is Host }.mapNotNull { it.device2.ipAddress?.uintIp }
            // Find min address or use router address + 1
            var candidateAddress = IPV4((usedIPAddresses.minOrNull() ?: thisIP.uintIp) + 1U)
            while (candidateAddress.uintIp in usedIPAddresses && candidateAddress in subnetNotNull) {
                candidateAddress = IPV4(candidateAddress.uintIp + 1U)
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

    fun getConnectionLine(other: Device) = thisConnectionLineMappings[other.id]!!

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
        val base = "r|" + super.serialize().substring(2)
        return "$base|$cidrPrefix|" + connections.joinToString("|", transform = { it.device2.id.toString() })
    }


    override fun toString(): String {
        return super.toString().replace("Device", "Router")
    }

    override fun routeTo(target: IPV4, visited: List<Device>): List<Device>? {
        // Some kind of loop exists
        if (this in visited) {
            return null
        }
        if (target in subnetNotNull) {
            if (target == ipAddress) {
                return visited + this
            } else {
                val targetDevice = connections.find { it.device2.ipAddress == target } ?: return null
                return visited + this + targetDevice.device2
            }
        }
        // Stupid bruteforce but who cares!
        val possibleRouters = this.connections.mapNotNull { it.device2 as? Router }
        val possibleRoutes = possibleRouters.mapNotNull {
            it.routeTo(target, visited + this)
        }

        return possibleRoutes.minByOrNull { it.size }
    }

    companion object {
        fun deserialize(serialized: String, parent: Pane): Router {
            val parts = serialized.split("|")
            val id = parts[1].toInt()
            val x = parts[2].toDouble().toInt()
            val y = parts[3].toDouble().toInt()
            val ip = parts[4].toUIntOrNull()
            val cidr = parts[5].toInt()
            val router = Router(id, x, y, parent)
            router.ipAddress = ip?.let { IPV4(it) }
            router.cidrPrefix = cidr
            return router
        }
    }

}
