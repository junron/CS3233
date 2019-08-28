package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class RaisingFlag extends Application {
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    // Create a pane 
    Pane pane = new Pane();

    // Add an image view and add it to pane
    ImageView imageView = new ImageView(String.valueOf(getClass().getResource("/singapore.png")));
    pane.getChildren().add(imageView);

    imageView.setX(0);
    imageView.setY(400);

    // Create a scene and place it in the stage
    Scene scene = new Scene(pane, 250, 400);
    primaryStage.setTitle("Raising Flag"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage

    //Add relevant code to animate the raising flag
    AnimationThread animationThread = new AnimationThread(imageView);
    animationThread.start();

  }

  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}

class AnimationThread extends Thread {
  private ImageView image;

  public AnimationThread(ImageView image) {
    this.image = image;
  }

  public void run() {
    while (true) {
      image.setY(image.getY() - 5);
      if(image.getY()<0) break;
      try {
        sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }
}