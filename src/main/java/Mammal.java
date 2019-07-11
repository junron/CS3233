public class Mammal {
  private String name;
  public Mammal(String name){
    this.name = name;
  }
  public String say(){
    return "What does the "+this.name+" say";
  }
  public String getName() {
    return name;
  }
}