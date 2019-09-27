package javafx;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Callback;
import math.Vectors;

import java.util.ArrayList;

public class LineAnimation extends AnimationTimer {
  private final Pane parent;
  private long startNanoTime;
  private boolean started;
  private double distanceToNextPoint;
  private double lineDirection;
  private int currentPointIndex = -1;
  private int pxRate;
  private ArrayList<Line> lines = new ArrayList<>();
  private Color color;
  private Line l;
  private Point2D[] points;
  private Callback<LineAnimation, Void> onComplete;


  public LineAnimation(Point2D[] points, int pxRate, Color color, Pane parent,
                       Callback<LineAnimation, Void> onComplete) {
    this.points = points;
    this.pxRate = pxRate;
    this.color = color;
    this.parent = parent;
    this.onComplete = onComplete;
  }

  public ArrayList<Line> getLines() {
    return lines;
  }

  public void setPxRate(int pxRate) {
    this.pxRate = pxRate;
  }

  private void nextPoint() {
    currentPointIndex++;
    if (currentPointIndex == points.length - 1) {
      onComplete.call(this);
      this.stop();
      return;
    }
    Point2D currentPoint = points[currentPointIndex];
    Vectors pointVector = new Vectors(points[currentPointIndex + 1].subtract(currentPoint));
    distanceToNextPoint = pointVector.magnitude();
    lineDirection = pointVector.getAngle();
    this.l = new Line(currentPoint.getX(), currentPoint.getY(), currentPoint.getX(), currentPoint.getY());
    this.l.setStroke(color);
    lines.add(l);
    parent.getChildren().add(l);
    started = false;
  }

  @Override
  public void start() {
    nextPoint();
    super.start();
  }


  @Override
  public void handle(long now) {
    if (!started) {
      startNanoTime = now;
      started = true;
    }
    double timeDelta = (now - startNanoTime) / 1E9;
    double duration = distanceToNextPoint / pxRate;
    Point2D endPoint = Vectors.constructWithMagnitude(lineDirection, (timeDelta / duration) * distanceToNextPoint)
            .add(points[currentPointIndex]);
    l.setEndY(endPoint.getY());
    l.setEndX(endPoint.getX());
    if (timeDelta > duration) {
      l.setEndY(points[currentPointIndex + 1].getY());
      l.setEndX(points[currentPointIndex + 1].getX());
      nextPoint();
    }
  }

}
