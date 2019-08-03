package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddressBook extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      System.out.println(getClass().getResource("/main.fxml"));
      AnchorPane root = FXMLLoader.load(getClass().getResource("/main.fxml"));
//      AnchorPane root = new AnchorPane();
      Scene scene = new Scene(root, 600, 400);
      primaryStage.setTitle("Title");
//      scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
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
