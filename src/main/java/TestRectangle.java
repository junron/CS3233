public class TestRectangle {
  public static void main(String[] args) {
    Rectangle r1 = new Rectangle(new Point(0,0), 2.5, 5);
    System.out.println(r1.contains(new Point(1,2)));
    r1.print();

    Rectangle r2 = new Rectangle(new Point(5,8), 7, 2);
    System.out.println(r1.contains(new Point(2,4)));
    r2.print();
  }
}
