package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      //      AnchorPane root = FXMLLoader.load(getClass().getResource("/main.fxml"));
      AnchorPane root = new AnchorPane();
      WebView webView = new WebView();
      webView.getEngine().load(getClass().getResource("/gmaps.html").toString());
      root.getChildren().add(webView);
      webView.getEngine().documentProperty().addListener(observable -> {
        System.out.println("load");
        // Call this function
        webView.getEngine().executeScript("addMarker(1.3068198,103.7679341,'NUSH')");
      });
      Scene scene = new Scene(root, 800, 400);
      primaryStage.setTitle("Title");
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
