package application

import javafx.AngleDisplay
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import optics.InteractiveOpticalRectangle
import optics.light.Ray
import optics.objects.Interactive
import optics.objects.OpticalRectangle
import utils.OpticsList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.function.Consumer

object Storage {
    @JvmField
    val rays = ArrayList<Ray>()

    @JvmField
    val opticalRectangles = OpticsList<InteractiveOpticalRectangle>()

    @JvmField
    var opticsTabController: OpticsTabController? = null

    @JvmField
    var rayTabController: RayTabController? = null

    @JvmField
    var maximumReflectionDepth = 1000

    @JvmField
    var showLabels = true

    @JvmField
    var isAnimating = false

    @JvmField
    var darkTheme = false

    @JvmField
    var parent: Pane? = null
    private var isMaximumDepthExceeded = false
    var offset = Point2D(0.0, 0.0)
        set(offset) {
            if (System.currentTimeMillis() - prevRender <= 100) {
                return
            }
            prevRender = System.currentTimeMillis()
            opticalRectangles.forEach(Consumer { obj: OpticalRectangle -> obj.reposition() })
            rays.forEach(Consumer { ray: Ray ->
                ray.reposition()
                rerenderRay(ray)
            })
            field = offset
        }

    @JvmField
    var intersectionPoints: MutableMap<Point2D, AngleDisplay> = HashMap()
    private var prevRender: Long = 0


    @JvmStatic
    fun rerenderRay(ray: Ray) {
        val future = ray.renderRays(opticalRectangles.deepClone())
        //Remove old lines
        ray.removeAllLines()
        handleRender(future)
    }

    @JvmStatic
    fun reRenderAll() {
        if (System.currentTimeMillis() - prevRender <= 10) {
            return
        }
        prevRender = System.currentTimeMillis()
        val futures = ArrayList<CompletableFuture<ArrayList<Node>>>()
        val lines = ArrayList<Node>()
        for (r in rays) {
            lines.addAll(r.lines)
            futures.add(r.renderRays(opticalRectangles.deepClone()))
        }
        //Remove old lines
        parent!!.children.removeAll(lines)
        val voidCompletableFuture =
            CompletableFuture.allOf(*futures.toTypedArray<CompletableFuture<*>>())

        //    Block operations
        voidCompletableFuture.join()
        for (future in futures) {
            if (handleRender(future)) break
        }
    }

    private fun handleRender(future: CompletableFuture<ArrayList<Node>>): Boolean {
        val result: ArrayList<Node>
        result = try {
            future.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return true
        } catch (e: ExecutionException) {
            e.printStackTrace()
            return true
        }
        if (result == null) {
            //          Cancel extra alerts
            if (isMaximumDepthExceeded) return true
            isMaximumDepthExceeded = true
            val alert = FxAlerts
                .showErrorDialog("Error",
                    "Outstanding move, but that's illegal",
                    "Maximum reflection depth " +
                        "exceeded")
            alert.showAndWait()
            return true
        }
        val finalResults = ArrayList<Node>()
        var prev: Node? = null
        for (node in result) {
            if (node is Circle && prev is AngleDisplay) {
                intersectionPoints[Point2D(
                    node.centerX, node.centerY)] = prev
            } else {
                node.isMouseTransparent = true
                finalResults.add(node)
            }
            prev = node
        }
        isMaximumDepthExceeded = false
        //  Add nodes
        parent!!.children.addAll(finalResults)
        return false
    }

    @JvmStatic
    fun clearAll() {
        offset = Point2D(0.0, 0.0)
        parent!!.children.removeAll(opticalRectangles)
        for (r in rays) {
            r.destroy()
        }
        opticalRectangles.clear()
        rays.clear()
        reRenderAll()
    }
}
