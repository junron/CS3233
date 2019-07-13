package application;

import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;

public class InitInput {
  public static void init(UserMovable c,boolean wasd){
    Input i = getInput();
    i.addAction(new UserAction("left") {
      @Override
      protected void onAction() {
        c.left();
      }
    },wasd? KeyCode.A:KeyCode.LEFT);
    i.addAction(new UserAction("right") {
      @Override
      protected void onAction() {
        c.right();
      }
    },wasd? KeyCode.D:KeyCode.RIGHT);
    i.addAction(new UserAction("up") {
      @Override
      protected void onAction() {
        c.up();
      }
    },wasd? KeyCode.W:KeyCode.UP);
    i.addAction(new UserAction("down") {
      @Override
      protected void onAction() {
        c.down();
      }
    },wasd? KeyCode.S:KeyCode.DOWN);
  }
  public static void init(UserMovable c){
    init(c,false);
  }
}
