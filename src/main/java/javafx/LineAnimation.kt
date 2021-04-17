package javafx

import javafx.animation.AnimationTimer
import javafx.geometry.Point2D
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.util.Callback
import math.Vectors

class LineAnimation(
    private val points: List<Point2D>,
    private var pxRate: Int,
    private val color: Color,
    private val parent: Pane,
    private val onComplete: (LineAnimation)->Unit
) : AnimationTimer() {
    private var startNanoTime: Long = 0
    private var started = false
    private var distanceToNextPoint = 0.0
    private var lineDirection = 0.0
    private var currentPointIndex = -1
    val lines = mutableListOf<Line>()
    private var l: Line? = null
    fun setPxRate(pxRate: Int) {
        this.pxRate = pxRate
    }

    private fun nextPoint() {
        currentPointIndex++
        if (currentPointIndex == points.size - 1) {
            onComplete(this)
            stop()
            return
        }
        val currentPoint = points[currentPointIndex]
        val pointVector =
            Vectors(points[currentPointIndex + 1].subtract(currentPoint))
        distanceToNextPoint = pointVector.magnitude()
        lineDirection = pointVector.angle
        l = Line(currentPoint.x, currentPoint.y, currentPoint.x, currentPoint.y)
        l!!.stroke = color
        lines.add(l!!)
        parent.children.add(l)
        started = false
    }

    override fun start() {
        nextPoint()
        super.start()
    }

    override fun handle(now: Long) {
        if (!started) {
            startNanoTime = now
            started = true
        }
        val timeDelta = (now - startNanoTime) / 1E9
        val duration = distanceToNextPoint / pxRate
        val endPoint = Vectors.constructWithMagnitude(lineDirection,
            timeDelta / duration * distanceToNextPoint)
            .add(points[currentPointIndex])
        l!!.endY = endPoint.y
        l!!.endX = endPoint.x
        if (parent.height > 300 && (endPoint.x < 0 || endPoint.x > parent.width || endPoint
                .y < 0 || endPoint.y > parent.height - 165)
        ) {
            currentPointIndex = points.size - 2
            nextPoint()
            return
        }
        if (timeDelta > duration) {
            l!!.endY = points[currentPointIndex + 1].y
            l!!.endX = points[currentPointIndex + 1].x
            nextPoint()
        }
    }
}
