
public class TestMammals {
  public static void main(String[] args) {
    Mammal[] ma = {new Dog(), new Mammal("Cat"), new Cat(), new Fox(), new Cat(), new Dog()};
    for(Mammal m : ma){
      System.out.println(m.say());
    }
  }
}
