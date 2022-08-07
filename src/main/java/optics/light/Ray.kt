package optics.light

import application.Storage
import application.Storage.rerenderRay
import javafx.Draggable
import javafx.KeyActions
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import optics.InteractiveOpticalRectangle
import optics.RealLine
import optics.objects.Refract
import serialize.Serializable
import utils.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class Ray(var realLine: RealLine, var color: Color, val parent: Pane) :
    LightSource,
    Serializable {
    private val onStateChange = mutableListOf<(Ray) -> Unit>()
    private var onDestroy: (() -> Unit)? = null
    private var onFocusStateChanged: ((Boolean) -> Unit)? = null
    val lines = mutableListOf<Node>()
    lateinit var circle: RayCircle
    var isInRefractiveMaterial = false

    init {
        addCircle()
    }

    companion object {
        fun deserialize(string: String, parent: Pane): Ray {
            val parts = string.split("\\|").toTypedArray()
            val x = parts[1].toDouble()
            val y = parts[2].toDouble()
            val red = parts[3].toDouble()
            val green = parts[4].toDouble()
            val blue = parts[5].toDouble()
            val angle = parts[6].toDouble()
            val realLine =
                RealLine(Point2D(x, y), Point2D.ZERO).copyWithAngle(angle)
            val color = Color.rgb((red * 255).toInt(),
                (green * 255).toInt(),
                (blue * 255).toInt())
            return Ray(realLine, color, parent)
        }
    }

    fun setOnFocusStateChanged(onFocusStateChanged: ((Boolean) -> Unit)?) {
        this.onFocusStateChanged = onFocusStateChanged
    }

    private fun addCircle() {
        if (::circle.isInitialized) parent.children.remove(circle)
        //    Circle at the start of the ray
        circle = RayCircle(realLine.start.x,
            realLine.start.y,
            realLine.angle.toDegrees(),
            this)
        circle.focusedProperty()
            .addListener { _: ObservableValue<out Boolean>?, _: Boolean?, state: Boolean ->
                onFocusStateChanged?.invoke(state)
            }
        KeyActions(circle, parent) {
            triggerDestroy()
        }
        Draggable(circle, parent) {
            triggerDestroy()
        }
        parent.children.add(circle)
    }

    fun addOnStateChange(handler: (Ray) -> Unit) {
        onStateChange.add(handler)
    }

    fun destroy() {
        parent.children.remove(circle)
        removeAllLines()
    }

    fun setOnDestroy(onDestroy: (() -> Unit)?) {
        this.onDestroy = onDestroy
    }

    private fun triggerDestroy() {
        removeAllLines()
        onDestroy?.invoke()
    }

    fun requestFocus() {
        circle.requestFocus()
    }

    private fun renderRaysThreaded(objects: OpticsList<InteractiveOpticalRectangle>): ObservableList<Node> {
        val nodes = FXCollections.observableArrayList<Node>()
        var intersectionData =
            Geometry.getNearestIntersection(realLine, objects)
        var refNum = 0
        var currentLine = realLine.copy().screenLine
        while (intersectionData.first != null) {
            val (opticalObject, iPoint) = intersectionData
            opticalObject ?: break
            iPoint ?: break
            if (refNum > Storage.maximumReflectionDepth) {
                return FXCollections.observableArrayList()
            }
            currentLine.stroke = color
            currentLine.endX = iPoint.toScreenPoint().x
            currentLine.endY = iPoint.toScreenPoint().y
            val transform = opticalObject.transform(this, iPoint) ?: break
            //        End of line
//            val normal =
//                opticalObject.drawNormal(transform.intersectionSideData, iPoint)
            val activeArea = Circle(iPoint.x, iPoint.y, 20.0)
            val angleDisplay = transform.angleDisplay
            angleDisplay?.isVisible = false
            //      Find next optical object for light to interact with
            //Can interact with current object if currently inside object and is refractor
            val nextOpticalObject =
                if (opticalObject is Refract && isInRefractiveMaterial) {
                    Geometry
                        .getNearestIntersection(transform.realLine,
                            objects,
                            opticalObject)
                } else {
                    Geometry
                        .getNearestIntersection(transform.realLine,
                            objects.getAllExcept(opticalObject))
                }
            //      Let next optical object be current optical object
            intersectionData = nextOpticalObject
            nodes.addAll(currentLine)
            // Only show labels if option enabled
//            if (Storage.showLabels) {
//                nodes.addAll(angleDisplay,
//                    activeArea)
//                if (normal != null) {
//                    nodes.add(normal)
//                }
//            }
            currentLine = transform.realLine.screenLine
            refNum++
        }
        currentLine.stroke = color
        nodes.add(currentLine)
        lines.addAll(nodes)
        isInRefractiveMaterial = false
        return nodes
    }

    override fun renderRays(objects: OpticsList<InteractiveOpticalRectangle>): CompletableFuture<ArrayList<Node>> {
        removeAllLines()
        val completableFuture = CompletableFuture<ArrayList<Node>>()
        Thread(Runnable {
            val nodes: ObservableList<Node>
            try {
                nodes = renderRaysThreaded(objects)
                nodes.forEach(Consumer { node: Node ->
                    node.viewOrder = 100.0
                })
            } catch (e: Exception) {
                println("Error")
                e.printStackTrace()
                completableFuture.completeExceptionally(e)
                return@Runnable
            }
            completableFuture.complete(ArrayList(nodes))
        }).start()
        return completableFuture
    }

    override fun removeAllLines() {
        parent.children.removeAll(lines)
        this.lines.clear()
    }

    override fun serialize(): String {
        //    x,y,rotation,r,g,b
        return "r|${realLine.start.x}|${realLine.start.y}|${color.red}|${color.green}|${color.blue}|${realLine.angle}"
    }

    private fun copy(newRealLine: RealLine) = Ray(newRealLine, color, parent)

    fun update(newRealLine: RealLine = realLine, newColor: Color = color) {
        this.realLine = newRealLine
        this.color = newColor
        removeAllLines()
        circle.centerX = newRealLine.start.toScreenPoint().x
        circle.centerY = newRealLine.start.toScreenPoint().y
        rerenderRay(this)
    }

    fun clone(move: Boolean): Ray {
        val add = if (move) Point2D(10.0, 10.0) else Point2D(0.0, 0.0)
        val newRealLine = RealLine(realLine.start + add, realLine.end + add)
        return copy(newRealLine)
    }


}
