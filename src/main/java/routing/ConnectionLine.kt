package routing

import javafx.geometry.Point2D
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.text.Text

class ConnectionLine(private val connection: Connection) : Pane() {

    private val l = Line(
        connection.router.centerX, connection.router.centerY,
        connection.device2.centerX, connection.device2.centerY
    )

    private val t = StackPane()

    init {
        t.children += Text(connection.router.ipAddress.toString())
        t.background = Background(BackgroundFill(Color.WHITE, null, null))
        this.children += l
        this.children += t
        repositionText()
        connection.router.onDrags += {
            l.startX = connection.router.centerX
            l.startY = connection.router.centerY
            repositionText()
        }
        connection.device2.onDrags += {
            l.endX = connection.device2.centerX
            l.endY = connection.device2.centerY
            repositionText()
        }
    }

    private fun repositionText() {
        val vec = Point2D(connection.device2.centerX, connection.device2.centerY).subtract(
            connection.router.centerX,
            connection.router.centerY
        ).normalize().multiply(100.0)

        this.t.layoutX = vec.x + connection.router.centerX
        this.t.layoutY = vec.y + connection.router.centerY
        // If y is possibly within router object, sub x to move it away
        if ((this.t.layoutY - connection.router.layoutY).toInt() in 0..connection.router.height.toInt()) {
            this.t.layoutX -= 20
        }
    }

    fun highlight() {
        l.stroke = Color.GREEN
    }

    fun unhighlight() {
        l.stroke = Color.BLACK
    }

    fun setLabelVisibility(show: Boolean) {
        this.t.isVisible = show
    }
}
