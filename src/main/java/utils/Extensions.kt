package utils

import application.Storage
import javafx.geometry.Point2D

operator fun Point2D.minus(other: Point2D): Point2D = this.subtract(other)
operator fun Point2D.plus(other: Point2D): Point2D = this.add(other)

// Real points
fun Point2D.toRealPoint(): Point2D =  this - Storage.offset
fun Point2D.toScreenPoint(): Point2D =  this + Storage.offset
