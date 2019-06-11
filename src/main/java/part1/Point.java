package part1;

public class Point {

  private double x, y;
  public final static int MIN = -20;
  public final static int MAX = 20;

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

  private static int randomize(int min, int max){
    int range = (max - min) + 1;
    return (int)(Math.random() * range) + min;
  }

  @Override
  public String toString() {
    return "("+this.x+", "+this.y+")";
  }
  public static Point midPoint(Point p, Point q){
    double midX = (q.getX()-p.getX())/2.0;
    double midY = (q.getY()-p.getY())/2.0;
    return new Point(midX,midY);
  }
  public double distanceTo(Point q){
    return Math.pow(Math.pow(this.x-q.x,2)+Math.pow(this.y-q.y,2),0.5);
  }
  public double angleTo(Point q){
    double a = q.x - this.x;
    double o = q.y - this.y;
    return Math.atan2(o,a);
  }

  public void move(double theta, double d){
    this.x = this.x+d*Math.cos(theta);
    this.y = this.y+d*Math.sin(theta);
  }
}