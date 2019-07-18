//Max.java: Find a maximum object
class Max {
  /** Return the maximum of two objects */
  public static ComparableCircle max(ComparableCircle o1, ComparableCircle o2) {
    return o1.compareTo(o2)>0 ? o1 : o2;
  }
}