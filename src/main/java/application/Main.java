package application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;

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
    Entity walls = entityBuilder().buildScreenBounds(150);
    walls.setType(Types.WALL);
    walls.addComponent(new CollidableComponent(true));

    getGameWorld().addEntity(walls);
  }
  @Override
  protected void initGame() {
//    getGameWorld().addEntityFactory(new ComponentFactory());
    Entity tank = new Entity();
    tank.addComponent(tankComponent);

    getGameWorld().addEntity(tank);
    initScreenBounds();
  }
  @Override
  protected void initInput(){
    InitInput.init(tankComponent,false);
  }

  public static void main(String[] args) {
    launch(args);
  }

}