package application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class Main extends GameApplication {
  private Entity player;
  private Entity enemy;

  @Override
  protected void initSettings(GameSettings settings) {
    settings.setWidth(800);
    settings.setHeight(600);
    settings.setTitle("Basic tank game");
    settings.setVersion("0.1");
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
  protected void initGameVars(Map<String, Object> vars) {
    vars.put("playerScore", 0);
    vars.put("player2Score", 0);
  }

  @Override
  protected void initPhysics() {
    getPhysicsWorld().setGravity(0, 0);

    getPhysicsWorld().addCollisionHandler(new CollisionHandler(Types.PLAYER, Types.BULLET) {
      @Override
      protected void onCollisionBegin(Entity a, Entity b) {
        if (a.equals(player)) {
          getGameState().increment("player2Score", 1);
        } else {
          getGameState().increment("playerScore", 1);
        }
        a.removeFromWorld();
        b.removeFromWorld();
        getGameScene().getViewport().shakeTranslational(5);
//        getGameTimer().runOnceAfter(() -> restartGame(), Duration.seconds(3));
      }
    });
  }

  @Override
  protected void initGame() {
    initScreenBounds();
    getGameWorld().addEntityFactory(new ComponentFactory());
    this.player = spawn("Tank", new SpawnData(new Point2D(0, 0)).put("isEnemy", false));
    this.enemy = spawn("Tank", new SpawnData(new Point2D(0, 0)).put("isEnemy", true));
    InitInput.init(this.player.getComponent(TankComponent.class), false);
    InitInput.init(this.enemy.getComponent(TankComponent.class), true);
  }

  @Override
  protected void initUI() {
    TextDisplay player1 = new TextDisplay("Player 1 score", 75, 550);
    player1.bind(getGameState().intProperty("playerScore").asString());

    TextDisplay player2 = new TextDisplay("Player 2 score", 725, 550);
    player2.bind(getGameState().intProperty("player2Score").asString());
  }

//  private void restartGame() {
//    getGameWorld().removeEntities(getGameWorld().getEntitiesByType(Types.BULLET));
//    enemy.removeFromWorld();
//    player.removeFromWorld();
//    player = spawn("Tank", new SpawnData(new Point2D(0, 0)).put("isEnemy", false));
//    enemy = spawn("Tank", new SpawnData(new Point2D(0, 0)).put("isEnemy", true));
//    InitInput.init(this.player.getComponent(TankComponent.class), false,true);
//    InitInput.init(this.enemy.getComponent(TankComponent.class), true,true);
//    initPhysics();
//  }


  public static void main(String[] args) {
    launch(args);
  }

}