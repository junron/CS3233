package application;

import com.almasb.fxgl.entity.component.Component;

public abstract class UserMovable extends Component {
  abstract public void left();
  abstract public void right();
  abstract public void forward();
  abstract public void backward();
}
