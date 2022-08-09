package routing

import javafx.scene.shape.Line

class ConnectionLine(connection: Connection) :
    Line(
        connection.router.centerX, connection.router.centerY,
        connection.device2.centerX, connection.device2.centerY
    ) {

    init {
        connection.router.onDrags += {
            this.startX = connection.router.centerX
            this.startY = connection.router.centerY
        }
        connection.device2.onDrags += {
            this.endX = connection.device2.centerX
            this.endY = connection.device2.centerY
        }
    }
}
