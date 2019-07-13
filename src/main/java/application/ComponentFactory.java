package application;

import com.almasb.fxgl.entity.EntityFactory;

public class ComponentFactory implements EntityFactory {
//  @Spawns("Bullet")
//  public Entity newBall(SpawnData data) {
//    Entity ball = FXGL.entityBuilder()
//                      .from(data)
//                      .type(Types.BULLET)
//                      .viewWithBBox(new Circle(3, Color.BLACK))
//                      .build();
//
//    PhysicsComponent ballPhysics = new PhysicsComponent();
//    ballPhysics.setBodyType(BodyType.DYNAMIC);
//
//    FixtureDef def = new FixtureDef().density(0.3f).restitution(1.0f);
//
//    ballPhysics.setFixtureDef(def);
//    ballPhysics.setOnPhysicsInitialized(() -> ballPhysics.setLinearVelocity(5 * 60, 5 * 60));
//
//    ball.addComponent(ballPhysics);
//    ball.addComponent(new CollidableComponent(true));
//    ball.addComponent(new BulletComponent());
//
//    return ball;
//  }


}
