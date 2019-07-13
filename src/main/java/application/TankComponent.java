package application;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

public class TankComponent extends UserMovable {

  private PhysicsComponent physics;
  private ViewComponent view;

  private Texture texture;
  private double speed;

  @Override
  public void onUpdate(double tpf) {
    speed = tpf*60;
  }
  @Override
  public void onAdded(){
    getEntity().getTransformComponent().setRotationOrigin(new Point2D(42, 42));

    texture = FXGL.getAssetLoader().loadTexture("player.png");
    view.setView(texture);
  }
  public void up() {
    getEntity().setRotation(270);
    getEntity().translateY(-5 * speed);
  }

  public void down() {
    getEntity().setRotation(90);
    getEntity().translateY(5 * speed);
  }

  public void left() {
    getEntity().setRotation(180);
    getEntity().translateX(-5 * speed);
  }

  public void right() {
    getEntity().setRotation(0);
    getEntity().translateX(5 * speed);
  }

//  public void shoot() {
//    FXGL.spawn("Bullet", new SpawnData(getEntity().getCenter()).put("direction", angleToVector()));
//  }

  private Point2D angleToVector() {
    double angle = getEntity().getRotation();
    if (angle == 0) {
      return new Point2D(1, 0);
    } else if (angle == 90) {
      return new Point2D(0, 1);
    } else if (angle == 180) {
      return new Point2D(-1, 0);
    } else {    // 270
      return new Point2D(0, -1);
    }
  }

}