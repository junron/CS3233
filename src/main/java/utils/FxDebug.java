package utils;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class FxDebug {
  public static void indicatePoint(Point2D point, Paint color, Pane parent) {
    parent.getChildren().add(new Circle(point.getX(), point.getY(), 3, color));
  }

  public static void indicatePoint(Point2D point, Pane parent) {
    indicatePoint(point, Color.RED, parent);
  }

  public static void indicatePoint(Circle c, Pane parent) {
    c.setStroke(Color.RED);
    parent.getChildren().add(c);
  }

  public static void indicateLine(Point2D point1, Point2D point2, Paint color, Pane parent) {
    Line l = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    l.setStroke(color);
    parent.getChildren().add(l);
  }

  public static void indicateLine(Line l, Paint color, Pane parent) {
    l.setStroke(color);
    parent.getChildren().add(l);
  }

}
