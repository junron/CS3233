package application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class Main extends GameApplication {
  private TankComponent tankComponent = new TankComponent();

  @Override
  protected void initSettings(GameSettings settings) {
    settings.setWidth(800);
    settings.setHeight(600);
    settings.setTitle("Basic tank game");
  }

  private void initScreenBounds() {
    Entity walls = entityBuilder()
            .viewWithBBox(new Rectangle(10, 10, Color.BLACK))
            .with(new CollidableComponent(true))
            .buildScreenBounds(10);
    walls.setType(Types.WALL);

    getGameWorld().addEntity(walls);
  }

  @Override
  protected void initGame() {
    initScreenBounds();
    getGameWorld().addEntityFactory(new ComponentFactory());
    PhysicsComponent pc = new PhysicsComponent();
//    TODO: move this into factory?
    Entity tank = FXGL.entityBuilder()
                      .type(Types.PLAYER)
                      .viewWithBBox(new Texture(AssetLoader.loadImage("player.png")))
                      .with(pc)
                      .with(new CollidableComponent(true))
                      .with(tankComponent)
                      .build();
    getGameWorld().addEntity(tank);
  }

  @Override
  protected void initInput() {
    Input i = InitInput.init(tankComponent, false);
    i.addAction(new UserAction("fire") {
      @Override
      protected void onActionBegin() {
        tankComponent.shoot();
      }
    }, KeyCode.SPACE);
  }

  @Override
  protected void initPhysics() {
    getPhysicsWorld().setGravity(0, 0);

    getPhysicsWorld().addCollisionHandler(new CollisionHandler(Types.PLAYER,Types.BULLET) {
      @Override
      protected void onCollisionBegin(Entity a, Entity b) {
        a.removeFromWorld();
        getGameScene().getViewport().shakeTranslational(5);
      }
    });
  }

  public static void main(String[] args) {
    launch(args);
  }

}