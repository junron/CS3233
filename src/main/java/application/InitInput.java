package application;

import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;

public class InitInput {
  static Input init(UserMovable c, boolean wasd) {
    Input i = getInput();
    i.addAction(new UserAction("left") {
      @Override
      protected void onAction() {
        c.left();
      }
    }, wasd ? KeyCode.A : KeyCode.LEFT);

    i.addAction(new UserAction("right") {
      @Override
      protected void onAction() {
        c.right();
      }
    }, wasd ? KeyCode.D : KeyCode.RIGHT);

    i.addAction(new UserAction("forward") {
      @Override
      protected void onActionBegin() {
        c.forward();
      }
      @Override
      protected void onActionEnd() {
        c.stop();
      }
    }, wasd ? KeyCode.W : KeyCode.UP);

    i.addAction(new UserAction("backward") {
      @Override
      protected void onActionBegin() {
        c.backward();
      }

      @Override
      protected void onActionEnd() {
        c.stop();
      }
    }, wasd ? KeyCode.S : KeyCode.DOWN);
    return i;
  }

  public static Input init(UserMovable c) {
    return init(c, false);
  }
}
