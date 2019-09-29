package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utils.ThreadPool;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      ThreadPool.initialize(100);
      // Minified.start("https://latency-check.nushhwboard.tk", "lightproject", "DWzgVAgG0bDyqb18BKA5IO6mriA_");

      ResourceBundle bundle = ResourceBundle.getBundle("langs", new Locale("en"));
      AnchorPane root = FXMLLoader.load(getClass().getResource("/main.fxml"), bundle);
      Scene mainScene = new Scene(root, 600, 400);
      mainScene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
      Scene preloader = new Scene(FXMLLoader.load(getClass().getResource("/splash.fxml")), 350, 250);
      primaryStage.setScene(preloader);
      primaryStage.setTitle("Ray Simulator");
      primaryStage.show();
      new Thread(() -> {
        try {
          Thread.sleep(7000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        Platform.runLater(() -> {
          primaryStage.setScene(mainScene);
          primaryStage.setMaximized(true);
        });
      }).start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void stop() throws Exception {
    super.stop();
    ThreadPool.getExecutorService().shutdown();
  }

}
