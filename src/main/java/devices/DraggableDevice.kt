package devices

import application.Main
import application.Storage
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Text
import serialize.Serializable


open class DraggableDevice(val id: Int, x: Int, y: Int, val parent: Pane, imagePath: String) : Serializable,
    StackPane() {

    val text: Text = Text("No IP")
    private var movementDelta: Point2D = Point2D.ZERO
    private val image = Image(Main::class.java.getResourceAsStream(imagePath), 100.0, 100.0, false, true)
    private fun isInUIArea() = layoutY + image.height >= parent.height - 165

    val centerX: Double
        get() = layoutX + this.image.width / 2

    val centerY: Double
        get() = layoutY + this.image.height / 2


    val onDestroys = mutableListOf<(() -> Unit)>()

    val onDrags = mutableListOf<(() -> Unit)>()


    init {

        this.children += text
        val imageview = ImageView(this.image)
        this.children += imageview

        this.layoutX = x.toDouble() - this.image.width / 2
        this.layoutY = y.toDouble() - this.image.height / 2
        prefHeight = this.image.height + 20
        prefWidth = this.image.width + 10

        StackPane.setAlignment(imageview, Pos.BOTTOM_CENTER)
        StackPane.setAlignment(text, Pos.TOP_CENTER)

        setOnMousePressed { event: MouseEvent ->
            movementDelta = Point2D(
                layoutX - event.sceneX, layoutY - event.sceneY
            )
            event.consume()
        }
        onMouseDragged = EventHandler { event: MouseEvent ->
            // Prevent changes when animating or connecting
            if (Storage.isAnimating || Storage.connectionMode) return@EventHandler
            // Prevent object from entering UI area
            val prevY = layoutY
            layoutX = event.sceneX + movementDelta.x
            layoutY = event.sceneY + movementDelta.y
            if (isInUIArea() &&  //        Except when moving object to trash
                event.sceneX <= parent.width - 82
            ) {
                layoutY = prevY
            }
            event.consume()
            onDrags.forEach { it() }
        }
        onMouseReleased = EventHandler { e: MouseEvent ->
            if (e.sceneY > parent.height - 165 && e.sceneX > parent.width - 82) {
                onDestroys.forEach { it() }
                parent.children.remove(this)
                e.consume()
            }
        }
    }

    fun focus() {
        this.border = Border(
            BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(2.0))
        )
    }

    fun unfocus() {
        this.border = null
    }
    
    open fun deviceDeleted(device: DraggableDevice) {
        println("This is $id, removed ${device.id}")
    }


    open fun deserialize(obj: String): DraggableDevice {
        TODO()
    }

    override fun serialize(): String {
        TODO()
    }

    override fun toString(): String {
        return "DraggableDevice(id=$id, x=${this.layoutX}, y=${this.layoutY})"
    }

    override fun equals(other: Any?): Boolean {
        return other is DraggableDevice && other.id == this.id
    }

    override fun hashCode(): Int {
        return this.id
    }

}