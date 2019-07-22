package optics.objects;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import math.Intersection;
import math.IntersectionSideData;
import math.Vectors;
import utils.Geometry;

public class Mirror extends OpticalRectangle {
  private Point2D movementDelta;
  private EventHandler<MouseEvent> onDrag;

  public Mirror(double x, double y, double width, double height, double rotation) {
    super(x, y, width, height);
    this.setRotate(rotation);
    this.setArcHeight(0);
    this.setArcWidth(0);
    this.setFill(Color.color(5 / 255.0, 213 / 255.0, 255 / 255.0, 0.28));
    this.setStrokeWidth(1);
    this.setStroke(Color.BLACK);
    this.setOnMousePressed(event-> movementDelta = new Point2D(this.getX()-event.getSceneX(),this.getY()-event.getSceneY()));
    this.setOnMouseDragged(event -> {
      this.setX(event.getSceneX()+movementDelta.getX());
      this.setY(event.getSceneY()+movementDelta.getY());
      if(onDrag==null) return;
      onDrag.handle(event);
    });
  }
  public void setOnDrag(EventHandler<MouseEvent> handler){
    this.onDrag = handler;
  }

  @Override
  public Line transform(Line l, Point2D iPoint) {
    l.setEndX(iPoint.getX());
    l.setEndY(iPoint.getY());
    IntersectionSideData iData = getIntersectionSideData(iPoint);
    double normalAngle = iData.normalVector.getAngle();
    double intersectionAngle = Intersection.getIntersectingAngle(iData, l);
    return Geometry.createLineFromPoints(iPoint, iPoint
            .add(Vectors.constructWithMagnitude(normalAngle - intersectionAngle, 1000)));
  }

  @Override
  public IntersectionSideData getIntersectionSideData(Point2D iPoint) {
    return Intersection.getIntersectionSide(iPoint, this);
  }

  @Override
  public Line drawNormal(IntersectionSideData iData, Point2D iPoint) {
    double normalLength = 50;
    Line l = Geometry.createLineFromPoints(iPoint, iPoint.add(iData.normalVector.multiply(normalLength / 2)));
    l.getStrokeDashArray().addAll(4d);
    return l;
  }
}
