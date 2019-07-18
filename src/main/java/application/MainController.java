package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import math.Intersection;
import math.Vectors;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

  @FXML
  private Rectangle mirror;

  private Line generated;

  @FXML
  private AnchorPane parent;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Vectors endPoint = Vectors.constructWithMagnitude(Math.toRadians(-20),400);
    Vectors start = new Vectors(10,250);
    parent.getChildren().add(new Circle(start.getX(),start.getY(),3,Color.GREEN));
    generated = new Line(start.getX(),start.getY(),endPoint.getX()+start.getX(),endPoint.getY()+start.getY());
    parent.getChildren().add(generated);
    Path intersection = (Path) Shape.intersect(generated,mirror);
    if(Intersection.hasIntersectionPoint(intersection)){
      Point2D point = Intersection.getIntersectionPoint(intersection,start);
      System.out.println(Intersection.getIntersectionSide(point,mirror,parent));
      Circle c = new Circle(point.getX(),point.getY(),3, Color.RED);
      parent.getChildren().add(c);
    }
    intersection.setFill(Color.PURPLE);
    System.out.println(Math.toDegrees(endPoint.getAngle()));
  }
}
