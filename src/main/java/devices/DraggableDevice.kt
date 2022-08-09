package devices

import application.Main
import application.Storage
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import serialize.Serializable

open class DraggableDevice(val id: Int, var x: Int, var y: Int, val parent: Pane, imagePath: String) : Serializable,
    ImageView(
        Image(Main::class.java.getResourceAsStream(imagePath), 100.0, 100.0, true, true)
    ) {
    
    private var movementDelta: Point2D = Point2D.ZERO
    private fun isInUIArea() = y + image.height >= parent.height - 165

    init {
        this.layoutX = x.toDouble() - this.image.width / 2
        this.layoutY = y.toDouble() - this.image.height / 2
        y = layoutY.toInt()
        x = layoutX.toInt()
        
        setOnMousePressed { event: MouseEvent ->
            movementDelta = Point2D(
                x - event.sceneX, y - event.sceneY
            )
            event.consume()
        }
        onMouseDragged = EventHandler { event: MouseEvent ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
            // Prevent object from entering UI area
            layoutX = event.sceneX + movementDelta.x
            layoutY = event.sceneY + movementDelta.y
            if (isInUIArea() &&  //        Except when moving object to trash
                event.sceneX <= parent.width - 82
            ) {
                layoutY = y.toDouble()
            }else{
                y = layoutY.toInt()
                x = layoutX.toInt()
            }
            event.consume()
            Storage.reRenderAll()
        }
        onMouseReleased = EventHandler { e: MouseEvent ->
            if (e.sceneY > parent.height - 165 && e.sceneX > parent.width - 82) {
                parent.children.remove(this)
                onDestroy?.let { it() }
            }
        }
    }
    

    var onDestroy: (() -> Unit)? = null

    open fun deserialize(obj: String): DraggableDevice {
        TODO()
    }

    override fun serialize(): String {
        TODO()
    }

}
