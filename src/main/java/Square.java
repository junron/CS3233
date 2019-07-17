public class Square extends GeometricObject implements Colorable {
  private double length;

  public Square(double length) {
    this.length = length;
  }

  @Override
  public void howToColor() {
    System.out.println("Color all four sides");
  }

  @Override
  public double getArea() {
    return length*length;
  }

  @Override
  public double getPerimeter() {
    return length*4;
  }
}
