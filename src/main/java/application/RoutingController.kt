package application

import javafx.fxml.FXML
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.Text
import routing.RouteTableEntry
import utils.IPV4

class RoutingController {

    @FXML
    private lateinit var title: Text

    @FXML
    private lateinit var grid: GridPane

    private fun makeCenteredText(s: String): Text {
        val text = Text(s)
        text.font = Font.font("System", 18.0)
        GridPane.setHalignment(text, HPos.CENTER)
        GridPane.setValignment(text, VPos.CENTER)
        return text
    }

    fun initialize(ip: IPV4, routeTableEntries: List<RouteTableEntry>, router: Boolean) {
        title.text = "Routing table for $ip"
        grid.add(makeCenteredText("Destination"), 0, 0)
        grid.add(makeCenteredText("Next"), 1, 0)
        grid.add(makeCenteredText("Num hops"), 2, 0)
        if (router) {
            grid.add(makeCenteredText("Interface"), 3, 0)
        }
        routeTableEntries.forEachIndexed { index, routeTableEntry ->
            val hops = if (routeTableEntry.numHops < 0) "No connection" else routeTableEntry.numHops.toString()
            grid.add(makeCenteredText(routeTableEntry.dest.toString()), 0, index + 1)
            grid.add(makeCenteredText(routeTableEntry.next?.toString() ?: "-"), 1, index + 1)
            grid.add(makeCenteredText(hops), 2, index + 1)
            if (router) {
                grid.add(makeCenteredText(routeTableEntry.iface?.toString() ?: "-"), 3, index + 1)
            }
        }
        grid.isGridLinesVisible = true
    }
}
