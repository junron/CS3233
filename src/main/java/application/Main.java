package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utils.ThreadPool;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
    try {
      ThreadPool.initialize(100);

      AnchorPane root = FXMLLoader.load(getClass().getResource("/main.fxml"));
      Scene scene = new Scene(root, 600, 400);
      scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
      primaryStage.setScene(scene);
      primaryStage.setTitle("Ray Simulator");
      primaryStage.setMaximized(true);
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void stop() throws Exception {
    super.stop();
    ThreadPool.getExecutorService().shutdown();
  }

  public static void main(String[] args) {
    launch(args);
  }

}
