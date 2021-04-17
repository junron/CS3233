package optics

import javafx.scene.shape.Line

class PreciseJavaFXLine(startX: Double, startY: Double, endX: Double, endY: Double) :
    Line(startX, startY, endX, endY) {

    var preciseAngle = 0.0

    constructor(l: Line) : this(l.startX, l.startY, l.endX, l.endY) {
        println("${l.startX}, ${l.startY}, ${l.endX}, ${l.endY}")
    }

    init {
        println("$startX, $startY, $endX, $endY")
    }
}
