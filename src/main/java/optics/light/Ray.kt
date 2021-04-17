package optics.light

import application.Storage
import javafx.Draggable
import javafx.KeyActions
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.Shape
import math.Intersection
import math.Vectors
import optics.InteractiveOpticalRectangle
import optics.PreciseJavaFXLine
import optics.objects.OpticalRectangle
import optics.objects.Refract
import serialize.Serializable
import utils.Geometry
import utils.OpticsList
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function

class Ray(var currentJavaFXLine: PreciseJavaFXLine, val parent: Pane) :
    LightSource,
    Serializable {
    private val onStateChange = mutableListOf<(Event)->Unit>()
    private var onDestroy: ((Event)->Unit)? = null
    private var onFocusStateChanged: ((Boolean)->Unit)? = null
    val lines = ArrayList<Node>()
    private var originalJavaFXLine: PreciseJavaFXLine
    private var origin: Point2D
    private var originalOrigin: Point2D
    private var endPoint: Point2D
    var angle: Double = 0.0
        set(angle) {
            updateLine(angle, originalOrigin)
            circle.rotate = angle
        }
    lateinit var circle: RayCircle
    var color: Color
    var isInRefractiveMaterial = false
    private var realX: Double
    private var realY: Double

    init {
        originalJavaFXLine = PreciseJavaFXLine(currentJavaFXLine.startX,
            currentJavaFXLine.startY,
            currentJavaFXLine.endX,
            currentJavaFXLine.endY)
        originalJavaFXLine.preciseAngle = currentJavaFXLine.preciseAngle
        origin = Point2D(currentJavaFXLine.startX, currentJavaFXLine.startY)
        originalOrigin =
            Point2D(currentJavaFXLine.startX, currentJavaFXLine.startY)
        endPoint = Point2D(currentJavaFXLine.endX, currentJavaFXLine.endY)
        addCircle()
        angle = Math.toDegrees(currentJavaFXLine.preciseAngle)
        color = Color.BLACK
        realX = currentJavaFXLine.startX - Storage.offset.x
        realY = currentJavaFXLine.startY - Storage.offset.y
        println(Storage.offset)
        addCircle()
    }

    fun setOnFocusStateChanged(onFocusStateChanged: ((Boolean)->Unit)?) {
        this.onFocusStateChanged = onFocusStateChanged
    }

    private fun addCircle() {
        if (::circle.isInitialized) parent.children.remove(circle)
        //    Circle at the start of the ray
        circle = RayCircle(origin.x, origin.y, angle, this)
        circle.focusedProperty()
            .addListener { _: ObservableValue<out Boolean>?, _: Boolean?, state: Boolean ->
                onFocusStateChanged?.invoke(state)
            }
        KeyActions(circle, { e: KeyEvent ->
            angle = circle.rotate
            updateLine(circle.rotate,
                Point2D(circle.centerX, circle.centerY))
            triggerStateChange(e)
        }, { e: Event -> triggerDestroy(e) }, parent)
        Draggable(circle, { e: Event ->
            angle = circle.rotate
            updateLine(circle.rotate,
                Point2D(circle.centerX, circle.centerY))
            triggerStateChange(e)
        }, { e: Event -> triggerDestroy(e) }, parent)
        parent.children.add(circle)
    }

    fun addOnStateChange(handler: (Event)->Unit) {
        onStateChange.add(handler)
    }

    private fun triggerStateChange(e: Event) {
        for (handler in onStateChange) {
            handler(e)
        }
    }

    fun destroy() {
        parent.children.remove(circle)
        removeAllLines()
    }

    fun setOnDestroy(onDestroy: ((Event)->Unit)?) {
        this.onDestroy = onDestroy
    }

    private fun triggerDestroy(e: Event) {
        removeAllLines()
        onDestroy?.invoke(e)
    }

    private fun updateLine(rotation: Double, startPoint: Point2D) {
        origin = startPoint
        originalOrigin = startPoint
        val l = PreciseJavaFXLine(startPoint.x, startPoint.y, 0.0, 0.0)
        l.preciseAngle = Math.toRadians(rotation)
        val lineVec = Vectors.constructWithMagnitude(l.preciseAngle, 250000.0)
        val endpoint = startPoint.add(lineVec)
        l.endX = endpoint.x
        l.endY = endpoint.y
        endPoint = endpoint
        currentJavaFXLine = l
        originalJavaFXLine = l
    }

    fun requestFocus() {
        circle.requestFocus()
    }

    private fun renderRaysThreaded(objects: OpticsList<InteractiveOpticalRectangle>): ObservableList<Node>? {
        val nodes = FXCollections.observableArrayList<Node>()
        resetCurrentLine()
        var opticalObject =
            Geometry.getNearestIntersection(currentJavaFXLine, objects)
        var refNum = 0
        while (opticalObject != null) {
            if (refNum > Storage.maximumReflectionDepth) {
                return null
            }
            currentJavaFXLine.stroke = color
            val intersection =
                Shape.intersect(currentJavaFXLine, opticalObject) as Path
            val iPoint = Intersection.getIntersectionPoint(intersection,
                Vectors(origin),
                !Intersection
                    .hasIntersectionPoint(origin, opticalObject))
            val transform = opticalObject.transform(this, iPoint) ?: break
            //        End of line
            val normal =
                opticalObject.drawNormal(transform.intersectionSideData, iPoint)
            val activeArea = Circle(iPoint.x, iPoint.y, 20.0)
            val angleDisplay = transform.angleDisplay
            angleDisplay.isVisible = false
            //      Find next optical object for light to interact with
            //Can interact with current object if currently inside object and is refractor
            val nextOpticalObject =
                if (opticalObject is Refract && isInRefractiveMaterial) Geometry
                    .getNearestIntersection(transform.preciseJavaFXLine,
                        objects,
                        opticalObject) else Geometry
                    .getNearestIntersection(transform.preciseJavaFXLine,
                        objects.getAllExcept(opticalObject))
            //        Final check for bugs
            //        Detect if ray is passing through an optical object (rays cant pass through mirrors)
            //        If detected, stop further rendering
            if (nextOpticalObject == null) {
                for (obj in objects) {
                    if (obj === opticalObject) continue
                    if (Intersection.hasExitPoint(Shape.intersect(transform.preciseJavaFXLine,
                            obj), Point2D(transform
                            .preciseJavaFXLine.startX,
                            transform.preciseJavaFXLine.startY))
                    ) {
                        if (nodes.size == 0) {
                            //              Ensure that there is at least 1 line
                            lines.add(currentJavaFXLine)
                            nodes.add(currentJavaFXLine)
                        }
                        //  Abort rendering
                        isInRefractiveMaterial = false
                        lines.addAll(nodes)
                        return nodes
                    }
                }
            }
            //      Let next optical object be current optical object
            opticalObject = nextOpticalObject
            nodes.addAll(currentJavaFXLine)
            // Only show labels if option enabled
            if (Storage.showLabels) {
                nodes.addAll(angleDisplay,
                    activeArea)
                if(normal != null){
                    nodes.add(normal)
                }
            }
            currentJavaFXLine = transform.preciseJavaFXLine
            origin = iPoint
            refNum++
        }
        currentJavaFXLine.stroke = color
        nodes.add(currentJavaFXLine)
        lines.addAll(nodes)
        isInRefractiveMaterial = false
        return nodes
    }

    private fun resetCurrentLine() {
        originalJavaFXLine.endX = endPoint.x
        originalJavaFXLine.endY = endPoint.y
        origin = originalOrigin
        currentJavaFXLine = originalJavaFXLine
    }

    override fun renderRays(objects: OpticsList<InteractiveOpticalRectangle>): CompletableFuture<ArrayList<Node>> {
        val completableFuture = CompletableFuture<ArrayList<Node>>()
        Thread(Runnable {
            val nodes: ObservableList<Node>?
            try {
                nodes = renderRaysThreaded(objects)
                nodes?.forEach(Consumer { node: Node ->
                    node.viewOrder = 100.0
                })
            } catch (e: Exception) {
                println("Error")
                e.printStackTrace()
                completableFuture.completeExceptionally(e)
                return@Runnable
            }
            completableFuture.complete(if (nodes == null) null else ArrayList(
                nodes))
        }).start()
        return completableFuture
    }

    override fun removeAllLines() {
        val lines = lines.toTypedArray()
        this.lines.clear()
        Platform.runLater { parent.children.removeAll(*lines) }
    }

    override fun serialize(): String {
        //    x,y,rotation,r,g,b
        return "r|" + realX + "|" + realY + "|" + color.red + "|" + color
            .green + "|" + color.blue + "|" + angle
    }

    override fun deserialize(string: String) {
        val parts = string.split("\\|").toTypedArray()
        val x = parts[1].toDouble()
        val y = parts[2].toDouble()
        val red = parts[3].toDouble()
        val green = parts[4].toDouble()
        val blue = parts[5].toDouble()
        val angle = parts[6].toDouble()
        realX = x
        realY = y
        this.angle = angle
        parent.children.removeAll(circle)
        addCircle()
        reposition()
        color = Color.rgb((red * 255).toInt(),
            (green * 255).toInt(),
            (blue * 255).toInt())
    }

    fun clone(move: Boolean): Ray {
        val add = if (move) Point2D(10.0, 10.0) else Point2D(0.0, 0.0)
        val l = PreciseJavaFXLine(Geometry.createLineFromPoints(
            originalOrigin.add(add), Vectors
                .constructWithMagnitude(originalJavaFXLine.preciseAngle,
                    250000.0)
                .add(
                    originalOrigin).add(add)))
        l.preciseAngle = originalJavaFXLine.preciseAngle
        val res = Ray(l, parent)
        res.color = color
        return res
    }

    fun setScreenX(x: Double) {
        circle.centerX = x
        realX = x - Storage.offset.x
        println("$realX, $realY")
    }

    fun setScreenY(y: Double) {
        circle.centerY = y
        realY = y - Storage.offset.y
        println("$realX, $realY")
    }

    fun reposition() {
        circle.centerX = realX + Storage.offset.x
        circle.centerY = realY + Storage.offset.y
        updateLine(circle.rotate, Point2D(circle.centerX, circle.centerY))
    }

}
