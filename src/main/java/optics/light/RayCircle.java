package optics.light;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class RayCircle extends Circle {
  private Ray r;

  RayCircle(double centerX, double centerY, double angle, Ray r) {
    super(centerX, centerY, 6, Color.rgb(255, 0, 0, 0.25));
    this.setRotate(angle);
    this.setStroke(Color.BLACK);
    this.r = r;
    this.setViewOrder(100);
  }

  public Ray getRay() {
    return r;
  }

  public Ray clone() {
    return this.r.clone(true);
  }
}
