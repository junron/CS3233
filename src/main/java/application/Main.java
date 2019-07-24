package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      AnchorPane root = FXMLLoader.load(getClass().getResource("/main.fxml"));
      Scene scene = new Scene(root, 400, 400);
      primaryStage.setTitle("Java analog clock");
      scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
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
