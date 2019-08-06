package optics.objects;

import javafx.Draggable;
import javafx.Rotatable;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import math.IntersectionSideData;
import optics.PreciseLine;
import optics.TransformData;
import optics.light.Ray;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Function;

public class Wall extends OpticalRectangle{
  private ArrayList<EventHandler<Event>> onStateChange = new ArrayList<>();
  private Function<Event, Void> onDestroy;

  public Wall(double x, double y, double width, double height, Pane parent,double rotation) {
    super(x, y, width, height);
    this.setRotate(rotation);
    this.setFill(Color.rgb(180,179,176));
    this.setStroke(Color.BLACK);
    this.parent = parent;
    new Draggable(this, this::triggerStateChange, this::triggerDestroy, parent);
    new Rotatable(this, this::triggerStateChange);
  }

  private void triggerStateChange(Event e) {
    for (EventHandler<Event> handler : this.onStateChange) {
      handler.handle(e);
    }
  }

  private void triggerDestroy(Event e) {
    this.onDestroy.apply(e);
  }

  @Override
  public TransformData transform(Ray r, Point2D iPoint) {
    PreciseLine l = r.getCurrentLine();
    l.setEndX(iPoint.getX());
    l.setEndY(iPoint.getY());
    return null;
  }

  @Override
  public IntersectionSideData getIntersectionSideData(Point2D iPoint) {
    return null;
  }

  @Override
  public Line drawNormal(IntersectionSideData iData, Point2D iPoint) {
    return null;
  }

  @Override
  public void addOnStateChange(EventHandler<Event> handler) {
    this.onStateChange.add(handler);
  }

  @Override
  public void setOnDestroy(Function<Event, Void> onDestroy) {
    this.onDestroy = onDestroy;
  }

  @Override
  public byte[] serialize() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(Character.BYTES + Double.BYTES * 5);
    byteBuffer.putChar('w');
    byteBuffer.putDouble(this.getX());
    byteBuffer.putDouble(this.getY());
    byteBuffer.putDouble(this.getWidth());
    byteBuffer.putDouble(this.getHeight());
    byteBuffer.putDouble(this.getRotate());
    return byteBuffer.array();
  }

  @Override
  public void deserialize(byte[] serialized) {
    ByteBuffer buffer = ByteBuffer.wrap(serialized);
    buffer.getChar();
    double x = buffer.getDouble();
    double y = buffer.getDouble();
    double width = buffer.getDouble();
    double height = buffer.getDouble();
    double angle = buffer.getDouble();
    this.setX(x);
    this.setY(y);
    this.setWidth(width);
    this.setHeight(height);
    this.setRotate(angle);
  }
  @Override
  public OpticalRectangle clone(boolean shiftPositions) {
    return new Wall(this.getX()+(shiftPositions?10:0),this.getY()+(shiftPositions?10:0),this.getWidth(),this.getHeight(),parent,this.getRotate());
  }
}
