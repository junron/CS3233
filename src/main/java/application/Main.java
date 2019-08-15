package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import storage.UserStorage;
public class Main extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      AnchorPane root = FXMLLoader.load(getClass().getResource("/main.fxml"));
      Scene scene = new Scene(root, 600, 400);
      primaryStage.setTitle("Title");
      scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
      primaryStage.setScene(scene);
      primaryStage.setResizable(false);
      primaryStage.show();

//      Initialize storage
      UserStorage.initialize();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    launch(args);
  }

}
