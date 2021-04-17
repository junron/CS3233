package application

import application.Storage.offset
import application.Storage.rays
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import math.Vectors
import utils.minus
import utils.plus
import utils.times
import java.net.URL
import java.util.*
import java.util.function.Consumer

class MainController : Initializable {
    @FXML
    private var parent: AnchorPane? = null

    @FXML
    private var rayTabController: RayTabController? = null

    @FXML
    private var generalTabController: GeneralTabController? = null

    @FXML
    private var opticsTabController: OpticsTabController? = null

    @FXML
    private var animationTabController: AnimationTabController? = null

    private var prevLocation: Point2D = Point2D(0.0, 0.0)
    override fun initialize(location: URL, resources: ResourceBundle?) {
        val parent = parent ?: return
        rayTabController!!.initialize(parent)
        generalTabController!!.initialize(parent)
        opticsTabController!!.initialize(parent)
        animationTabController!!.initialize(parent)
        Storage.opticsTabController = opticsTabController
        Storage.rayTabController = rayTabController
        Storage.parent = parent
        parent.onMousePressed = EventHandler { event: MouseEvent ->
            if (!Storage.isAnimating) prevLocation =
                Point2D(event.sceneX, event.sceneY)
        }
        parent.onMouseDragged = EventHandler { event: MouseEvent ->
            if (Storage.isAnimating) return@EventHandler
            val newOffset =
                (Point2D(event.sceneX, event.sceneY) - prevLocation) * 5.0
            offset += newOffset
            prevLocation = Point2D(event.sceneX, event.sceneY)
            rays.forEach { 
                it.update(it.realLine.copy(), it.color)
            }
        }
        parent.onMouseMoved = EventHandler { event: MouseEvent ->
            val coords = Point2D(event.sceneX, event.sceneY)
            val remove = ArrayList<Point2D>()
            for ((key, value) in Storage.intersectionPoints) {
                if (!parent.children.contains(value)) {
                    remove.add(key)
                    continue
                }
                if (Vectors.distanceSquared(key, coords) < 400) {
                    value.isVisible = true
                    value.layoutX = event.sceneX + 7
                    value.layoutY = event.sceneY + 7
                } else {
                    value.isVisible = false
                }
            }
            remove.forEach(Consumer { point2D: Point2D? ->
                Storage.intersectionPoints.remove(point2D)
            })
        }
    }
}
