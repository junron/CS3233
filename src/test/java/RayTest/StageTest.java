package RayTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import util.TestThreadPool;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class StageTest extends ApplicationTest {
  private Stage stage;

  @Override
  public void start(Stage stage) {
    TestThreadPool.initialize(100);

    AnchorPane root;
    try {
      root = FXMLLoader.load(getClass().getResource("/main.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(root, 600, 400);
    scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
    stage.setScene(scene);
    stage.setTitle("Ray Simulator");
    stage.setMaximized(true);
    this.stage = stage;
    stage.show();
  }

  @Test
  public void hasTitle() {
    assertEquals("Ray Simulator", stage.getTitle());
  }
}
