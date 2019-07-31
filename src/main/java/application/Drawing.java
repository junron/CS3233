package application;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

public class Drawing extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      double[] point = new double[]{300.0, 100.0};
      Polyline pLine = new Polyline();
      pLine.getPoints().addAll(300.0, 100.0);
      pLine.setStroke(Color.BLACK);
      ObservableList<Double> points = pLine.getPoints();

      AnchorPane root = new AnchorPane();
      root.getChildren().add(pLine);
      Scene scene = new Scene(root, 600, 200);
      scene.setOnKeyPressed(event -> {
        switch (event.getCode().toString()) {
          case "DOWN": {
            point[1] += 10;
            points.addAll(point[0], point[1]);
            break;
          }
          case "UP": {
            point[1] -= 10;
            points.addAll(point[0], point[1]);
            break;
          }
          case "LEFT": {
            point[0] -= 10;
            points.addAll(point[0], point[1]);
            break;
          }
          case "RIGHT": {
            point[0] += 10;
            points.addAll(point[0], point[1]);
            break;
          }
        }
      });
      primaryStage.setTitle("Fun Drawing!");
      primaryStage.setScene(scene);
      primaryStage.setResizable(false);
      primaryStage.show();
      root.requestFocus();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }

}
