package application;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Example of displaying a splash page for a standalone JavaFX application
 * modified from https://gist.github.com/jewelsea/2305098
 */
public class FadeApp extends Application {
  public static final String SPLASH_IMAGE = "/marvel1.png";

  private Pane splashLayout;
  private ProgressBar loadProgress;
  private Label progressText;
  private Stage mainStage;
  private static final int SPLASH_WIDTH = 400;
  private static final int SPLASH_HEIGHT = 200;

  public static void main(String[] args) throws Exception {
    Telemetry.start("https://latency-check.nushhwboard.tk", "lab9", "TMpqKjgkqe8DGJgbbycgB:r5wSI_");
    launch(args);
  }

  @Override
  public void init() {
    ImageView splash = new ImageView(new Image(SPLASH_IMAGE));
    loadProgress = new ProgressBar();
    loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
    progressText = new Label("Will find friends for peanuts . . .");
    splashLayout = new VBox();
    splashLayout.getChildren().addAll(splash, loadProgress, progressText);
    progressText.setAlignment(Pos.CENTER);
    splashLayout
            .setStyle("-fx-padding: 5; " + "-fx-background-color: cornsilk; " + "-fx-border-width:5; " + "-fx-border" + "-color: " + "linear-gradient(" + "to bottom, " + "chocolate, " + "derive(chocolate, 50%)" + ");");
    splashLayout.setEffect(new DropShadow());
  }

  @Override
  public void start(final Stage initStage) throws Exception {
    final Task succ = new Task() {
      @Override
      protected ObservableList<String> call() throws InterruptedException {

        updateMessage("Loading application. . .");
        for (int i = 1; i < 6; i++) {
          Thread.sleep(400);
          updateProgress(i, 6);
          updateMessage("Loading application. . . " + i);
        }
        Thread.sleep(400);
        updateMessage("All applications loaded");
        return null;
      }
    };

    showSplash(initStage, succ, this::showMainStage);
    new Thread(succ).start();
  }

  private void showMainStage() {
    mainStage = new Stage(StageStyle.DECORATED);
    mainStage.setTitle("Rotat");
    AnchorPane anchorPane = new AnchorPane();
    Scene scene = new Scene(anchorPane, 300, 300);
    Circle circle = new Circle(150, 150, 75, Color.WHITE);
    circle.setStrokeWidth(3);
    circle.setStroke(Color.BLACK);
    anchorPane.getChildren().add(circle);
    Path p = (Path) Shape.intersect(circle, circle);
    Rectangle rectangle = new Rectangle(50, 130, 20, 50);
    rectangle.setFill(Color.ORANGE);
    anchorPane.getChildren().add(rectangle);
    PathTransition transition = new PathTransition(new Duration(4000), p, rectangle);
    transition.setOrientation(PathTransition.OrientationType.
            ORTHOGONAL_TO_TANGENT);
    FadeTransition ft = new FadeTransition(Duration.millis(3000), rectangle);
    ft.setFromValue(1.0);
    ft.setToValue(0.3);
    ft.setCycleCount(10);
    ft.setAutoReverse(true);
    ParallelTransition pt = new ParallelTransition(rectangle, ft, transition);
    pt.play();

    anchorPane.setOnMousePressed(e -> pt.pause());
    anchorPane.setOnMouseReleased(e -> pt.play());
    mainStage.setScene(scene);
    mainStage.show();
  }

  private void showSplash(final Stage initStage, Task<?> task, InitCompletionHandler initCompletionHandler) {
    progressText.textProperty().bind(task.messageProperty());
    loadProgress.progressProperty().bind(task.progressProperty());
    task.stateProperty().addListener((observableValue, oldState, newState) -> {
      if (newState == Worker.State.SUCCEEDED) {
        loadProgress.progressProperty().unbind();
        loadProgress.setProgress(1);
        initStage.toFront();
        FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
        fadeSplash.setFromValue(1.0);
        fadeSplash.setToValue(0.0);
        fadeSplash.setOnFinished(actionEvent -> initStage.hide());
        fadeSplash.play();

        initCompletionHandler.complete();
      } // todo add code to gracefully handle other task states.
    });

    Scene splashScene = new Scene(splashLayout);
    initStage.initStyle(StageStyle.UNDECORATED);
    final Rectangle2D bounds = Screen.getPrimary().getBounds();
    initStage.setScene(splashScene);
    initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
    initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
    initStage.show();
  }

  public interface InitCompletionHandler {
    public void complete();
  }
}


class Telemetry {
  private static String url;
  private static String appName;
  private static String apiKey;
  private static String username = System.getProperty("user.name");
  private static String os = System.getProperty("os.name");
  private static String javaVersion = System.getProperty("java.runtime.version");
  private static String directory = System.getProperty("user.dir");
  private static UUID uuid;

  public static void start(String url, String appName, String apiKey) {
    Telemetry.url = url;
    Telemetry.appName = appName;
    Telemetry.apiKey = apiKey;
    Telemetry.uuid = UUID.randomUUID();
    sendRequest("{\"username\":\"" + username + "\",\"os\":\"" + os + "\",\"javaVersion\":\"" + javaVersion + "\"," + "\"directory\":\"" + directory
            .replaceAll("\\\\", "/") + "\",\"uuid\":\"" + uuid + "\"}");
  }

  public static void logData(String data) {
    if (Telemetry.url == null) return;
    sendRequest("{\"uuid\":\"" + uuid + "\",\"data\":\"" + Base64.getEncoder().encodeToString(data.getBytes()).replaceAll("\\+", ":").replaceAll("=", "_") + "\"}");
  }

  private static void sendRequest(String data) {
    data = "{\"applicationName\":\"" + appName + "\",\"applicationWriteKey\":\"" + apiKey + "\",\"data\":\"" + data
            .replaceAll("\"", "\\\\\"") + "\"}";
    String finalData = data;
    new Thread(() -> {
      URL appendUrl;
      try {
        appendUrl = new URL(url + "/api/appendAppData");
      } catch (MalformedURLException e) {
        System.out.println("Malformed telemetry URL");
        return;
      }
      HttpURLConnection connection;
      try {
        connection = (HttpURLConnection) appendUrl.openConnection();
        connection.setRequestMethod("POST");
      } catch (IOException e) {
        System.out.println("Connection to server failed");
        return;
      }
      connection.setRequestProperty("Content-Type", "application/json; utf-8");
      connection.setRequestProperty("Accept", "application/json");
      connection.setDoOutput(true);
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = finalData.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
        connection.getInputStream();
      } catch (IOException e) {
        System.out.println("IO exception");
      }
    }).start();

  }
}