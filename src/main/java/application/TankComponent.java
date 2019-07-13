package application;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;

import static java.lang.Math.toRadians;

public class TankComponent extends UserMovable {

  private PhysicsComponent physics;

  private double speed;
  private int angle = 0;
  private double height, width;

  @Override
  public void onUpdate(double tpf) {
    speed = tpf * 60;
  }

  @Override
  public void onAdded() {
    this.height = getEntity().getHeight();
    this.width = getEntity().getWidth();
    getEntity().getTransformComponent().setRotationOrigin(new Point2D(width / 2, height / 2));
  }

  public void forward() {
    Point2D rotation = angleToVector();
    double x = rotation.getX();
    double y = rotation.getY();
    getEntity().translateY(5 * y);
    getEntity().translateX(5 * x);
  }

  public void backward() {
    Point2D rotation = angleToVector();
    double x = rotation.getX();
    double y = rotation.getY();
    getEntity().translateY(-5 * y);
    getEntity().translateX(-5 * x);
  }

  public void left() {
    angle -= 2;
//    Clockwise
    getEntity().rotateBy(2);
  }

  public void right() {
    angle += 2;
    //    Anti-Clockwise
    getEntity().rotateBy(-2);
  }

  public void shoot() {
    System.out.println(getEntity().getTransformComponent());
    Point2D bulletPoint = getEntity().getCenter().add(getFiringPoint());
    System.out.println(bulletPoint);
    FXGL.spawn("Bullet", new SpawnData(bulletPoint).put("angle", angleToVector()));
  }

  private Point2D angleToVector() {
    double angle = toRadians(getEntity().getRotation());
    return new Point2D(Math.cos(angle), Math.sin(angle));
  }

  private Point2D getFiringPoint() {
    double angle = toRadians(this.angle);
//    Move point width/2 across
    double x = (this.width / 2) * Math.cos(angle);
    double y = (this.width / 2) * Math.sin(angle);
    return new Point2D(x, -y);
  }

}