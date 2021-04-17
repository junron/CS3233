package application

import application.Storage.rerenderRay
import javafx.LineAnimation
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.scene.shape.Line
import optics.PreciseJavaFXLine
import java.util.concurrent.ExecutionException

class AnimationTabController {
    @FXML
    var pixelsPerSecond: TextField? = null
    private var pxRate = 300
    lateinit var parent: Pane
    private var currentAnimation: LineAnimation? = null
    fun initialize(parent: Pane) {
        this.parent = parent
        pixelsPerSecond!!.textProperty()
            .addListener { _: ObservableValue<out String>?, _: String?, value: String ->
                val pxRate: Int = try {
                    value.toInt()
                } catch (e: NumberFormatException) {
                    return@addListener
                }
                this.pxRate = pxRate
                if (currentAnimation != null) {
                    currentAnimation!!.setPxRate(this.pxRate)
                }
            }
    }

    fun startAnimation() {
        val r = Storage.rayTabController!!.focusedRay
        if (r == null) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Animation error"
            alert.headerText = "No ray selected"
            alert.contentText = "Please select a ray to animate"
            alert.showAndWait()
            return
        }
        val future = r.renderRays(Storage.opticalRectangles.deepClone())
        var points = mutableListOf<Point2D>()
        try {
            val nodes = future.get()
            if (nodes == null) {
                val alert = Alert(Alert.AlertType.ERROR)
                alert.title = "Animation error"
                alert.headerText = "Cannot animate to infinite"
                alert.contentText =
                    "Please resolve maximum reflection depth exceeded errors\n before animating"
                alert.showAndWait()
                return
            }
            points = convertLineToPoints(nodes)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return
        } catch (e: ExecutionException) {
            e.printStackTrace()
            return
        }
        r.removeAllLines()
        val lineAnimation = LineAnimation(points, pxRate, r
            .color, parent) { lineAnimation1: LineAnimation ->
            parent.children.removeAll(lineAnimation1.lines)
            Storage.isAnimating = false
            currentAnimation = null
            rerenderRay(r)
        }
        //Lock movement while animating
        Storage.isAnimating = true
        currentAnimation = lineAnimation
        lineAnimation.start()
    }

    companion object {
        fun convertLineToPoints(lines: ArrayList<Node>): ArrayList<Point2D> {
            val result = ArrayList<Point2D>()
            for (line in lines) {
                if (line !is PreciseJavaFXLine) continue
                result.add(Point2D((line as Line).startX,
                    (line as Line).startY))
                result.add(Point2D((line as Line).endX, (line as Line).endY))
            }
            return result
        }
    }
}
