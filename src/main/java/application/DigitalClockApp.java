/* ....Show License.... */
package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A digital clock application that demonstrates JavaFX animation, images, and
 * effects.
 */
public class DigitalClockApp extends Application {
  public static final String IMAGE =
          "/DigitalClock-background.png";

  private Clock clock;

  public Parent createContent() {
    Group root = new Group();
    // background image
    String url = getClass().getResource(IMAGE).toExternalForm();
    ImageView background = new ImageView(new Image(url));
    background.setLayoutY(200);
    background.setLayoutX(7);
    // digital clock
    clock = new Clock(Color.ORANGERED, Color.rgb(50, 50, 50));
    clock.setLayoutX(45+7);
    clock.setLayoutY(386);
    clock.getTransforms().add(new Scale(0.83f, 0.83f, 0, 0));
    // Color picker
    DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
    Date date = new Date();
    final ColorPicker colorPicker = new ColorPicker(Color.GREEN);
    final Label coloredText = new Label("My Digital Java Clock");
    final Label dateText = new Label(dateFormat.format(date));
    coloredText.setAlignment(Pos.CENTER);
    dateText.setAlignment(Pos.CENTER);
    dateText.setFont(new Font(28));
    coloredText.setFont(new Font(53));
    Color c = colorPicker.getValue();
    coloredText.setTextFill(c);
    dateText.setTextFill(c);

    colorPicker.setOnAction((EventHandler<javafx.event.ActionEvent>) t -> {
      Color newColor = colorPicker.getValue();
      coloredText.setTextFill(newColor);
      dateText.setTextFill(newColor);
    });
    VBox outerVBox = new VBox(coloredText, dateText, colorPicker);
    outerVBox.setAlignment(Pos.CENTER);
    outerVBox.setSpacing(20);
    outerVBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
    // add background and clock to sample
    root.getChildren().addAll(background,clock,outerVBox);
//    root.getChildren().addAll(coloredText,dateText,colorPicker);
    return root;
  }

  public void play() {
    clock.play();
  }

  @Override public void stop() {
    clock.stop();
  }

  @Override public void start(Stage primaryStage) throws Exception {
//    primaryStage.setResizable(false);
    primaryStage.setScene(new Scene(createContent()));
    primaryStage.show();
    primaryStage.setTitle("CS3233 OOPII Lab 1: Digital Clock");
    play();
  }

  /**
   * Java main for when running without JavaFX launcher
   */
  public static void main(String[] args) {
    launch(args);
  }
}