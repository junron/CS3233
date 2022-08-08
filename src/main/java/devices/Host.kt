package devices

import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import serialize.Serializable

open class Host(val id: Int, val x: Int, val y: Int, val parent: Pane) : Serializable, ImageView() {
    
    var onDestroy: (()->Unit)? = null 
    
    open fun deserialize(obj: String): Host{
        TODO()
    }
    
    override fun serialize(): String{
        TODO()
    }
    
}
