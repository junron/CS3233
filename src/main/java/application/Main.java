package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      GridPane root = new GridPane();
      root.setAlignment(Pos.CENTER);
      String[] labels = new String[]{"Investment Amount: ","Number of years: ","Annual Interest Rate: ","Future value: "};
      for(int i=0;i<4;i++){
        root.add(new Label(labels[i]),0,i);
      }
      TextField invAmt = new TextField();
      TextField numYears = new TextField();
      TextField interest = new TextField();
      TextField future = new TextField();
      Button calc = new Button("Calculate");
      root.add(invAmt,1,0);
      root.add(numYears ,1,1);
      root.add(interest,1,2);
      root.add(future,1,3);
      root.add(calc,1,4);
      GridPane.setHalignment(calc, HPos.RIGHT);

      calc.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          double invest = Double.parseDouble(invAmt.getText());
          double mInterest = Double.parseDouble(interest.getText());
          double years = Double.parseDouble(numYears.getText());
          future.setText(String.valueOf(invest*Math.pow(1+mInterest,years*12)));
        }
      });

      Scene scene = new Scene(root, 300, 150);
      primaryStage.setTitle("Investment calculator");
      primaryStage.setScene(scene);
      primaryStage.setResizable(false);
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    launch(args);
  }

}
