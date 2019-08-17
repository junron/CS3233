package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import storage.CarStorage;
import storage.UserStorage;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      //      Initialize storage
      UserStorage.initialize();
      CarStorage.initialize();


      AnchorPane root = FXMLLoader.load(getClass().getResource("/main.fxml"));
      Scene scene = new Scene(root, 600, 400);
      ScreenController.initialize(scene, getClass());
      ScreenController.addScreen("main");
      ScreenController.addScreen("signup");
      ScreenController.addScreen("admin");
      ScreenController.addScreen("addcar");
      ScreenController.activate("main");
      primaryStage.setTitle("Title");
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
