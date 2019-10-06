package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import networking.NetworkingClient;
import utils.Minified;
import utils.ThreadPool;

import java.util.ArrayList;

public class Main extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      ThreadPool.initialize(100);
      Minified.start("https://latency-check.nushhwboard.tk", "lightproject", "DWzgVAgG0bDyqb18BKA5IO6mriA_");

      AnchorPane root = FXMLLoader.load(getClass().getResource("/main.fxml"));
      Scene mainScene = new Scene(root, 600, 400);
      mainScene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
      primaryStage.setTitle("Ray Simulator");
      Scene preloader = new Scene(FXMLLoader.load(getClass().getResource("/splash.fxml")), 529, 294);
      primaryStage.setScene(preloader);
      primaryStage.show();
      new Thread(() -> {
        try {
          Thread.sleep(7500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        Platform.runLater(() -> {
          primaryStage.setScene(mainScene);
          primaryStage.setMaximized(true);
          Storage.clearAll();
          ArrayList<Node> remove = new ArrayList<>();
          Storage.parent.getChildren().forEach(child->{
            if(child instanceof Line || child instanceof GridPane){
              remove.add(child);
            }
          });
          Storage.parent.getChildren().removeAll(remove);
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
    NetworkingClient.shutdown();
  }

}
