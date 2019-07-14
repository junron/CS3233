package application;

import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;

public class InitInput {
  static Input init(UserMovable c, boolean wasd) {
    String suffix = wasd ? "wasd" : "";
    Input i = getInput();

    i.addAction(new UserAction("left"+suffix) {
      @Override
      protected void onAction() {
        if(c==null) return;
        c.left();
      }
    }, wasd ? KeyCode.A : KeyCode.LEFT);

    i.addAction(new UserAction("right"+suffix) {
      @Override
      protected void onAction() {
        if(c==null) return;
        c.right();
      }
    }, wasd ? KeyCode.D : KeyCode.RIGHT);

    i.addAction(new UserAction("forward"+suffix) {
      @Override
      protected void onActionBegin() {
        if(c==null) return;
        c.forward();
      }

      @Override
      protected void onActionEnd() {
        if(c==null) return;
        c.stop();
      }
    }, wasd ? KeyCode.W : KeyCode.UP);

    i.addAction(new UserAction("backward"+suffix) {
      @Override
      protected void onActionBegin() {
        if(c==null) return;
        c.backward();
      }

      @Override
      protected void onActionEnd() {
        if(c==null) return;
        c.stop();
      }
    }, wasd ? KeyCode.S : KeyCode.DOWN);
    i.addAction(new UserAction("fire"+suffix) {
      @Override
      protected void onActionBegin() {
        if(c==null) return;
        c.shoot();
      }
    }, wasd ? KeyCode.F : KeyCode.SPACE);
    return i;
  }

}
