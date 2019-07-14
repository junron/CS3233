package application;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;
import static java.lang.Math.toRadians;

public class TankComponent extends UserMovable {

  private PhysicsComponent physics;

  private double speed = 300;
  private int angle = 0;
  private double height, width;
  private Rectangle2D bounds;

  @Override
  public void onAdded() {
    this.height = getEntity().getHeight();
    this.width = getEntity().getWidth();
    this.bounds = getGameScene().getViewport().getVisibleArea();
    getEntity().getTransformComponent().setRotationOrigin(new Point2D(width / 2, height / 2));


    physics.setBodyType(BodyType.DYNAMIC);
    FixtureDef fd = new FixtureDef();
    fd.setFriction(1f);
    physics.setFixtureDef(fd);


  }

  public void forward() {
    Point2D movement = angleToVector().multiply(this.speed);
    physics.setLinearVelocity(movement);
  }

  public void backward() {
    Point2D movement = angleToVector().multiply(-this.speed);
    physics.setLinearVelocity(movement);
  }

  public void stop(){
    physics.setLinearVelocity(new Point2D(0,0));
  }

  public void left() {
    angle += 2;
//    Clockwise
    physics.overwriteAngle(angle);
  }

  public void right() {
    angle -= 2;
    //    Anti-Clockwise
    physics.overwriteAngle(angle);
  }

  void shoot() {
    Point2D bulletPoint = getEntity().getCenter().add(angleToVector().multiply(this.width/2+10));
    FXGL.spawn("Bullet", new SpawnData(bulletPoint).put("angle", angleToVector()));
  }

  private Point2D angleToVector() {
    double angle = toRadians(this.angle);
    return new Point2D(Math.cos(angle), Math.sin(angle));
  }


}