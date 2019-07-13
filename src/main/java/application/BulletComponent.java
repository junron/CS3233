package application;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;

public class BulletComponent extends Component {

  private PhysicsComponent physics;

  @Override
  public void onUpdate(double tpf) {
    limitVelocity();
    checkOffscreen();
  }

  private void limitVelocity() {
    if (Math.abs(physics.getLinearVelocity().getX()) < 5 * 60) {
      physics.setLinearVelocity(Math.signum(physics.getLinearVelocity().getX()) * 5 * 60,
              physics.getLinearVelocity().getY());
    }

    if (Math.abs(physics.getLinearVelocity().getY()) > 5 * 60 * 2) {
      physics.setLinearVelocity(physics.getLinearVelocity().getX(),
              Math.signum(physics.getLinearVelocity().getY()) * 5 * 60);
    }
  }

  // this is a hack:
  // we use a physics engine, so it is possible to push the ball against a wall
  // so that it gets moved outside of the screen
  private void checkOffscreen() {
    if (getEntity().getBoundingBoxComponent().isOutside(FXGL
            .getGameScene()
            .getViewport()
            .getVisibleArea())) {

      getEntity().getComponent(PhysicsComponent.class).overwritePosition(new Point2D(
              FXGL.getAppWidth() / 2,
              FXGL.getAppHeight() / 2
      ));
    }
  }
}