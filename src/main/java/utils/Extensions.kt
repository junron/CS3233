package utils

import application.Storage
import javafx.geometry.Point2D
import kotlin.math.PI

operator fun Point2D.minus(other: Point2D): Point2D = this.subtract(other)
operator fun Point2D.plus(other: Point2D): Point2D = this.add(other)
operator fun Point2D.times(x: Double): Point2D = this.multiply(x)

// Real points
fun Point2D.toRealPoint(): Point2D = this - Storage.offset
fun Point2D.toScreenPoint(): Point2D = this + Storage.offset
fun Double.toDegrees() = this * 180 / PI
fun Double.toRadians() = this * PI / 180
