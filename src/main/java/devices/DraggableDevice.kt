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

open class DraggableDevice(val id: Int, x: Int, y: Int, val parent: Pane, imagePath: String) : Serializable,
    ImageView(
        Image(Main::class.java.getResourceAsStream(imagePath), 100.0, 100.0, false, true)
    ) {
    
    private var movementDelta: Point2D = Point2D.ZERO
    private fun isInUIArea() = layoutY + image.height >= parent.height - 165
    
    val centerX : Double
        get() = layoutX + this.image.width/2
    
    val centerY : Double
        get() = layoutY + this.image.height/2
    
    
        

    var onDestroy: (() -> Unit)? = null
    val onDrags = mutableListOf<(() -> Unit)>()
    
    

    init {
        this.layoutX = x.toDouble() - this.image.width / 2
        this.layoutY = y.toDouble() - this.image.height / 2
        
        setOnMousePressed { event: MouseEvent ->
            movementDelta = Point2D(
                layoutX - event.sceneX, layoutY - event.sceneY
            )
            event.consume()
        }
        onMouseDragged = EventHandler { event: MouseEvent ->
            // Prevent changes when animating
            if (Storage.isAnimating) return@EventHandler
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
                parent.children.remove(this)
                onDestroy?.let { it() }
            }
        }
    }
    
    

    open fun deserialize(obj: String): DraggableDevice {
        TODO()
    }

    override fun serialize(): String {
        TODO()
    }

    override fun toString(): String {
        return "DraggableDevice(id=$id, x=$x, y=$y)"
    }

    override fun equals(other: Any?): Boolean {
        return other is DraggableDevice && other.id == this.id
    }

}
