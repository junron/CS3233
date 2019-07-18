public class ComparableCircle extends Circle implements Comparable {

  ComparableCircle(double radius){
    super(radius);
  }

  @Override
  public int compareTo(Object o) {
    if(o instanceof ComparableCircle){
      return (int) (this.getArea() - ((ComparableCircle) o).getArea());
    }else{
      return -1;
    }
  }
}
