package application;

import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class ComponentFactory implements EntityFactory {

  @Spawns("Bullet")
  public Entity newBullet(SpawnData data){
    return entityBuilder()
            .from(data)
            .type(Types.BULLET)
            .with(new ProjectileComponent(data.get("angle"),600))
            .with(new CollidableComponent(true))
            .with(new OffscreenCleanComponent())
            .viewWithBBox(new Circle(3, Color.BLACK))
            .build();
  }


}
