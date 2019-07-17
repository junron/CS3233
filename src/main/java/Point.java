public class Point {

  private double x, y;
  public final static int MIN = -20;
  public final static int MAX = 20;

  public Point(){
    this(randomize(MIN,MAX),randomize(MIN,MAX));
  }

  public Point(double x, double y){
    this.x = x;
    this.y = y;
  }

  public double getX(){
    return this.x;
  }

  public double getY(){
    return this.y;
  }

  public String toString(){
    return "(" + x + ", " + y + ")";
  }

  private static int randomize(int min, int max){
    int range = (max - min) + 1;
    return (int)(Math.random() * range) + min;
  }

  public Point copy(){
    return new Point(this.x, this.y);
  }
}