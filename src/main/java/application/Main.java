package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import storage.CarStorage;
import storage.TransactionStorage;
import storage.UserStorage;

public class Main extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      //      Initialize storage
      UserStorage.initialize();
      CarStorage.initialize();
      TransactionStorage.initialize();

      AnchorPane root = new AnchorPane();
      Scene scene = new Scene(root, 600, 400);
      ScreenController.initialize(scene, getClass());
      ScreenController.addScreen("main");
      ScreenController.addScreen("signup");
      ScreenController.addScreen("admin");
      ScreenController.addScreen("addcar");
      ScreenController.addScreen("gallery");
      ScreenController.addScreen("dashboard");
      ScreenController.addScreen("adminchart");
      ScreenController.activate("main");
      primaryStage.setTitle("Car sharing");
      scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
      primaryStage.setScene(scene);
      primaryStage.setResizable(false);
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
