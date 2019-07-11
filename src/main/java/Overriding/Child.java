package Overriding;

public class Child extends Parent{
  String packString = "Child Package String";
  public String pubString = "Child public String";
  protected String proString = "Child protected String";

  static String packStaticString = "Child Package static String";
  public static String pubStaticString = "Child public static String";
  protected static String proStaticString = "Child protected static String";
}

// Doesn't work, cannot inherit from final class
/*
public class FinalChild extends FinalParent{

}
*/

class Test {
  public static void main(String[] args){
    Child c = new Child();
    System.out.println(Child.packStaticString);
    System.out.println(Child.proStaticString);
    System.out.println(Child.pubStaticString);
    System.out.println(c.packString);
    System.out.println(c.proString);
    System.out.println(c.pubString);
  }
}