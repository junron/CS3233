package application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;

public class Main extends GameApplication {
  private TankComponent tankComponent = new TankComponent();

  @Override
  protected void initSettings(GameSettings settings) {
    settings.setWidth(800);
    settings.setHeight(600);
    settings.setTitle("Basic bouncing ball");
  }
  private void initScreenBounds() {
    Entity walls = entityBuilder()
            .viewWithBBox(new Rectangle(10,10, Color.BLACK))
            .with(new CollidableComponent(true))
            .buildScreenBounds(150);
    walls.setType(Types.WALL);

    getGameWorld().addEntity(walls);
  }
  @Override
  protected void initGame() {
    initScreenBounds();
    getGameWorld().addEntityFactory(new ComponentFactory());
    Entity tank = FXGL.entityBuilder()
            .viewWithBBox(new Texture(AssetLoader.loadImage("player.png")))
            .build();
    tank.addComponent(tankComponent);

    getGameWorld().addEntity(tank);
  }
  @Override
  protected void initInput(){
    Input i = InitInput.init(tankComponent,false);
    i.addAction(new UserAction("fire") {
      @Override
      protected void onActionBegin() {
        tankComponent.shoot();
      }
    }, KeyCode.SPACE);
  }

  public static void main(String[] args) {
    launch(args);
  }

}