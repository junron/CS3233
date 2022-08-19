package application

import application.Storage.clearAll
import application.Storage.devices
import application.Storage.resetConnectionLines
import devices.Device
import devices.Host
import devices.Router
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.control.TitledPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import routing.ConnectionLine
import routing.RouteTableEntry
import serialize.FileOps
import serialize.Serializable
import serialize.deserialize
import utils.*
import java.io.IOException
import kotlin.random.Random


class GeneralTabController {

    private var focusedObject: Device? = null

    @FXML
    private var newRouter: Button? = null

    @FXML
    private var newHost: Button? = null

    @FXML
    private lateinit var save: Button

    @FXML
    private lateinit var load: Button

    @FXML
    private var clearAll: Button? = null

    @FXML
    private var connectionMode: CheckBox? = null

    @FXML
    private lateinit var showIfaceIP: CheckBox

    @FXML
    private lateinit var hostPane: TitledPane

    @FXML
    private lateinit var routerPane: Pane

    @FXML
    private lateinit var cidrPrefix: TextField

    @FXML
    private lateinit var ipAddr: TextField

    @FXML
    private lateinit var targetIp: TextField

    @FXML
    private lateinit var sendPacket: Button

    @FXML
    private lateinit var showRoute: Button

    @FXML
    private lateinit var pane: AnchorPane

    private fun connectionModeEnabled(): Boolean = connectionMode?.isSelected ?: false

    fun initialize(parent: Pane) {
        pane.background = Background(BackgroundFill(Color.WHITE, null, null))

        save.onMouseClicked = EventHandler { event: MouseEvent? ->
            val allObjects: MutableList<Serializable> = devices.toMutableList()
            try {
                FileOps.save(allObjects, parent.scene.window as Stage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        load.onMouseClicked = EventHandler { e: MouseEvent? ->
            val data: ArrayList<String> = try {
                FileOps.load(parent.scene.window as Stage)
            } catch (ex: IOException) {
                ex.printStackTrace()
                return@EventHandler
            } ?: return@EventHandler
            clearAll()
            deserialize(data.map { it.trim() }, parent).forEach {
                addObject(it, parent)
            }
        }
        clearAll!!.onMouseClicked = EventHandler { event: MouseEvent? -> clearAll() }


        connectionMode!!.onMouseClicked = EventHandler {
            Storage.connectionMode = connectionModeEnabled()
            unfocus()
        }

        showIfaceIP.selectedProperty().addListener { _, _, newValue -> Storage.showIfaceIP = newValue }


        newHost!!.onMouseClicked = EventHandler { _: MouseEvent? ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            val h = Host(
                Random.nextInt(),
                Math.floorDiv(parent.width.toInt(), 2),
                Math.floorDiv(parent.height.toInt(), 2),
                parent
            )

            addObject(h, parent)
        }
        newRouter!!.onMouseClicked = EventHandler { _: MouseEvent? ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            val r = Router(
                Random.nextInt(),
                Math.floorDiv(parent.width.toInt(), 2),
                Math.floorDiv(parent.height.toInt(), 2),
                parent
            )

            r.onMouseClicked = EventHandler clickHandler@{ evt: MouseEvent ->
                val other = focusedObject
                focusedObject?.unfocus()
                focusedObject = r
                r.focus()
                evt.consume()
                if (!connectionModeEnabled()) {
                    updateHostPane(r)
                    return@clickHandler
                }
                if (other != null && other != r) {
                    r.addConnection(other)
                    if (other is Router) {
                        other.addConnection(r)
                    }
                }
            }
            addObject(r, parent)
        }

        parent.onMouseClicked = EventHandler {
            unfocus()
        }

        // Handlers for host pane
        this.ipAddr.textProperty().addListener { _, _, newValue ->
            val device = focusedObject
            val parsed = newValue.toIPV4OrNull()
            if (parsed != null) {
                // Don't allow loopback, broadcast or link local
                if (parsed in loopback || parsed in broadcast || parsed in linkLocal) {
                    return@addListener
                }

                if (device is Host) {
                    // Cannot change IP address to invalid one
                    if (device.connectedRouter?.subnet?.let { parsed !in it } == true) {
                        return@addListener
                    }
                }
                focusedObject?.ipAddress = parsed
            }
        }

        this.cidrPrefix.textProperty().addListener { _, _, newValue ->
            newValue.toIntOrNull()?.let {
                (focusedObject as? Router)?.cidrPrefix = it
            }
        }

        this.targetIp.textProperty().addListener { _, _, newValue ->
            val parsed = newValue.toIPV4OrNull()
            this.sendPacket.isDisable = parsed == null || focusedObject?.ipAddress == null
        }

        this.sendPacket.onMouseClicked = EventHandler {
            resetConnectionLines()
            val ip = this.targetIp.text.toIPV4()
            focusedObject?.let { device ->
                val connectionLines = if (ip in broadcast) {
                    // Undirected broadcast
                    device.subnet?.let { routeToSubnet(it, device) }
                } else {
                    device.routeTo(ip, emptyList())?.filter { it.ipAddress != device.ipAddress }?.let { route ->
                        processRoute(device, route)
                    }
                }
                if (connectionLines == null) {
                    FxAlerts.error("No route to host!", "No route to host!").show()
                    return@EventHandler
                }
                connectionLines.forEach {
                    it.highlight()
                }
            }
        }

        this.showRoute.onMouseClicked = EventHandler {
            val thisDevice = focusedObject ?: return@EventHandler
            val ipAddress = thisDevice.ipAddress ?: return@EventHandler
            val loader = FXMLLoader(Main::class.java.getResource("/routing.fxml"))
            val controller = RoutingController()
            loader.setController(controller)
            val root = loader.load<AnchorPane>()
            val dialog = Stage()
            dialog.initModality(Modality.APPLICATION_MODAL)
            dialog.scene = Scene(root)

            val routingTable = Storage.subnets.sortedBy { it.ip.uintIp }.map { subnet ->
                val route = thisDevice.routeTo(subnet.ip, emptyList())?.filter {
                    ipAddress != it.ipAddress
                }
                val iface = when (thisDevice) {
                    is Host -> null
                    is Router -> if (route.isNullOrEmpty()) {
                        null
                    } else {
                        // Get iface ip going to first router on route
                        thisDevice.ifaceIPMapping[route.first()]
                    }
                }
                if (route == null) {
                    RouteTableEntry(subnet, null, -1, iface)
                } else if (route.isEmpty()) {
                    RouteTableEntry(subnet, null, 0, iface)
                } else {
                    RouteTableEntry(subnet, route.first().ipAddress, route.size, iface)
                }
            }
            controller.initialize(ipAddress, routingTable, thisDevice is Router)
            dialog.showAndWait()

        }

        hostPane.isVisible = false
    }

    private fun routeToSubnet(subnet: Subnet, device: Device): List<ConnectionLine>? {
        val route = device.routeTo(subnet.ip, emptyList()) ?: return null
        if (route.isEmpty()) return emptyList()
        val finalRouter = (route.last() as? Router) ?: return processRoute(device, route)
        val connectionLines = if (finalRouter == device) {
            mutableListOf()
        } else {
            processRoute(device, route).toMutableSet()

        }
        finalRouter.connections.forEach {
            val deviceIp = it.device2.ipAddress ?: return@forEach
            if (deviceIp == device.ipAddress) {
                return@forEach
            }
            if (deviceIp in subnet) {
                val routeToDevice = finalRouter.routeTo(deviceIp, emptyList())?.filter { 
                    it.ipAddress != finalRouter.ipAddress
                } ?: return@forEach
                connectionLines += processRoute(finalRouter, routeToDevice)
            }
        }
        return connectionLines.toList()
    }

    private fun processRoute(
        device: Device,
        route: List<Device>,
    ): MutableList<ConnectionLine> {
        var currentDevice = device
        val connectionLines = mutableListOf<ConnectionLine>()
        for (hop in route) {
            if (currentDevice is Host && hop is Router) {
                // Routers have connection lines back to host
                connectionLines.add(hop.getConnectionLine(currentDevice))
            } else if (currentDevice is Router && hop is Router) {
                // Two connection lines between routers
                connectionLines.add(hop.getConnectionLine(currentDevice))
                connectionLines.add(currentDevice.getConnectionLine(hop))
            } else if (currentDevice is Router) {
                // One connection line between router and final host
                connectionLines.add(currentDevice.getConnectionLine(hop))
            } else {
                // This case should not happen
                throw Error("Hosts should not be able to connect to hosts directly.")
            }
            currentDevice = hop
        }
        return connectionLines
    }

    private fun updateHostPane(device: Device) {
        this.ipAddr.text = device.ipAddress?.toString() ?: "No IP"
        this.targetIp.text = ""
        sendPacket.isDisable = true
        cidrPrefix.isDisable = false
        ipAddr.isDisable = false

        if (device is Router) {
            hostPane.text = "Router"
            routerPane.isVisible = true
            cidrPrefix.text = device.cidrPrefix.toString()

            //Disable changing CIDR and IP if device has connections
            if (device.connections.isNotEmpty()) {
                cidrPrefix.isDisable = true
                ipAddr.isDisable = true
            }
        } else {
            hostPane.text = "Host"
            routerPane.isVisible = false
        }

        showRoute.isDisable = device.ipAddress == null
        hostPane.isVisible = true

    }

    private fun unfocus() {
        focusedObject?.unfocus()
        focusedObject = null
        hostPane.isVisible = false
        resetConnectionLines()
    }

    private fun addObject(obj: Device, parent: Pane) {
        devices.add(obj)
        obj.onDestroys += {
            if (focusedObject == obj) focusedObject = null
            devices.remove(obj)
        }
        parent.children.add(obj)
        obj.onMouseClicked = EventHandler clickHandler@{ evt: MouseEvent ->
            val other = focusedObject
            focusedObject?.unfocus()
            obj.focus()
            evt.consume()
            focusedObject = obj
            if (!connectionModeEnabled()) {
                updateHostPane(obj)
                return@clickHandler
            }
            // Routers can connect to anything
            if (obj is Router) {
                if (other != null && other != obj) {
                    obj.addConnection(other)
                    if (other is Router) {
                        other.addConnection(obj)
                    }
                }
            } else if (other is Router) {
                // Hosts can only connect to routers
                other.addConnection(obj)
            }
        }
        obj.requestFocus()
    }

}
