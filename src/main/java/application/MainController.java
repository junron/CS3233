package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class MainController implements Initializable {

  @FXML
  private AnchorPane parent;

  @FXML
  private Text time;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Date date = new Date();
    int second = date.getSeconds();
    int secondAngle = (int)((second/60.0)*360)-90;
    Line l = createLine(createPointFromAngle(secondAngle,130));
    l.setStroke(Color.RED);

    int minute = date.getMinutes();
    int mAngle = (int)((minute/60.0)*360)-90;
    Line l2 = createLine(createPointFromAngle(mAngle,110));
    l2.setStroke(Color.BLUE);

    int hour = date.getHours();
    int hAngle = (int)(((hour%12)/12.0)*360)-90;
    Line l3 = createLine(createPointFromAngle(hAngle,90));
    l3.setStroke(Color.GREEN);

    time.setText(hour+":"+minute+":"+second);

    parent.getChildren().addAll(l,l2,l3);
  }

  public Line createLine(Point2D p2){
    return new Line(200,200,200+p2.getX(),200+p2.getY());
  }

  public Point2D createPointFromAngle(int angle,int distance){
    double angleR = Math.toRadians(angle);
    return new Point2D(Math.cos(angleR)*distance,Math.sin(angleR)*distance);
  }
}

