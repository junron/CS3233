class Rectangle implements Shape, Printable{
  private Point point;
  private double height;
  private double width;


  Rectangle(Point point, double width, double height) {
    this.point = point;
    this.height = height;
    this.width = width;
  }
  public double getArea(){
    return this.width * this.height;
  }
  public double getPerimeter(){
    return this.width*2 + this.height*2;
  }

  public boolean contains(Point p){
    double x = p.getX();
    double y = p.getY();
    if(x<point.getX()) return false;
    if(y<point.getY()) return false;
    if(x>(point.getX()+this.width)) return false;
    return !(y > (point.getY() + this.height));
  }

  public void print(){
    System.out.printf("Width: %.6f%n",this.width);
    System.out.printf("Height: %.6f%n",this.height);
    System.out.printf("Top-Left: %s%n",this.point.toString());
  }
}