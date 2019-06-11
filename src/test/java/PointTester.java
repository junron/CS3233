import org.junit.Test;
import part1.Point;

import static junit.framework.TestCase.assertEquals;

public class PointTester {
  @Test
  public void ToString(){
    Point p = new Point(0,0);
    assertEquals("(0.0, 0.0)",p.toString());
  }
  @Test
  public void angleBetween(){
    Point p = new Point(0,0);
    Point p1 = new Point(1,1);
    Point p01 = new Point(1,0);
    assertEquals(0.7853981633974483,p.angleTo(p1));
    assertEquals(0.0,p.angleTo(p01));
  }
  @Test
  public void distanceTo(){
    Point p = new Point(1,1);
    Point p1 = new Point(5,4);
    assertEquals(5.0,p.distanceTo(p1));
    assertEquals(0.0,p1.distanceTo(p1));
  }
  @Test
  public void moveTo(){
    Point p = new Point(1,1);
    Point p1 = new Point(5,4);
    p.move(p.angleTo(p1),p.distanceTo(p1));
    assertEquals("(5.0, 4.0)",p.toString());
  }
  @Test
  public void moveTo2(){
    Point p = new Point(1,1);
    Point p1 = new Point(0,0);
    p1.move(p1.angleTo(p),p1.distanceTo(p));
    assertEquals("(1.0000000000000002, 1.0)",p1.toString());
  }
  @Test
  public void midpoint(){
    Point p = new Point(2,1);
    Point p1 = new Point(0,0);
    assertEquals("(1.0, 0.5)",Point.midPoint(p1,p).toString());
  }

}
