package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AnalogClockv2 extends Application {
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    ClockPane clock = new ClockPane(); // Create a clock

    HBox hBox = new HBox(5);
    Button btStop = new Button("Stop");
    Button btStart = new Button("Start");
    btStart.setOnAction(event -> clock.clockThread.startClock());
    btStop.setOnAction(event -> clock.clockThread.stopClock());
    hBox.getChildren().addAll(btStop, btStart);
    hBox.setAlignment(Pos.CENTER);

    BorderPane pane = new BorderPane();
    pane.setCenter(clock);
    pane.setBottom(hBox);

    // Create a scene and place it in the stage
    Scene scene = new Scene(pane, 250, 300);
    primaryStage.setTitle("Java Analog Clock Reloaded"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage


    clock.widthProperty().addListener(ov -> {
      clock.setW(pane.getWidth());
    });

    clock.heightProperty().addListener(ov -> {
      clock.setH(pane.getHeight());
    });
  }

  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }

  class ClockThread extends Thread {
    private int sleepTime;
    private ClockPane clockPane;
    private boolean running = false;
    private boolean started = false;

    ClockThread(int sleepTime, ClockPane clockPane) {
      this.sleepTime = sleepTime;
      this.clockPane = clockPane;
    }

    public void startClock() {
      if (!this.started) start();
      this.running = true;
      this.started = true;
    }

    public void stopClock() {
      this.running = false;
    }

    public void run() {
      while (true) {
        if (this.running){
          Platform.runLater(() -> this.clockPane.setCurrentTime());
        }
        try {
          sleep(sleepTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  class ClockPane extends Pane {
    private int hour;
    private int minute;
    private int second;
    public ClockThread clockThread;

    // Clock pane's width and height
    private double w = 250, h = 250;

    private int sleepTime = 50;

    /**
     * Construct a default clock with the current time
     */
    public ClockPane() {
      setCurrentTime();
      this.clockThread = new ClockThread(sleepTime, this);
    }

    /**
     * Construct a clock with specified hour, minute, and second
     */
    public ClockPane(int hour, int minute, int second) {
      this.hour = hour;
      this.minute = minute;
      this.second = second;
      paintClock();
    }

    /**
     * Return hour
     */
    public int getHour() {
      return hour;
    }

    /**
     * Set a new hour
     */
    public void setHour(int hour) {
      this.hour = hour;
      paintClock();
    }

    /**
     * Return minute
     */
    public int getMinute() {
      return minute;
    }

    /**
     * Set a new minute
     */
    public void setMinute(int minute) {
      this.minute = minute;
      paintClock();
    }

    /**
     * Return second
     */
    public int getSecond() {
      return second;
    }

    /**
     * Set a new second
     */
    public void setSecond(int second) {
      this.second = second;
      paintClock();
    }

    /**
     * Return clock pane's width
     */
    public double getW() {
      return w;
    }

    /**
     * Set clock pane's width
     */
    public void setW(double w) {
      this.w = w;
      paintClock();
    }

    /**
     * Return clock pane's height
     */
    public double getH() {
      return h;
    }

    /**
     * Set clock pane's height
     */
    public void setH(double h) {
      this.h = h;
      paintClock();
    }

    /* Set the current time for the clock */
    public void setCurrentTime() {
      // Construct a calendar for the current date and time
      Calendar calendar = new GregorianCalendar();

      // Set current hour, minute and second
      this.hour = calendar.get(Calendar.HOUR_OF_DAY);
      this.minute = calendar.get(Calendar.MINUTE);
      this.second = calendar.get(Calendar.SECOND);

      paintClock(); // Repaint the clock
    }

    /**
     * Paint the clock
     */
    private void paintClock() {
      // Initialize clock parameters
      double clockRadius = Math.min(w, h) * 0.8 * 0.5;
      double centerX = w / 2;
      double centerY = h / 2;

      // Draw circle
      Circle circle = new Circle(centerX, centerY, clockRadius);
      circle.setFill(Color.WHITE);
      circle.setStroke(Color.BLACK);
      Text t1 = new Text(centerX - 5, centerY - clockRadius + 12, "12");
      Text t2 = new Text(centerX - clockRadius + 3, centerY + 5, "9");
      Text t3 = new Text(centerX + clockRadius - 10, centerY + 3, "3");
      Text t4 = new Text(centerX - 3, centerY + clockRadius - 3, "6");

      // Draw second hand
      double sLength = clockRadius * 0.8;
      double secondX = centerX + sLength * Math.sin(second * (2 * Math.PI / 60));
      double secondY = centerY - sLength * Math.cos(second * (2 * Math.PI / 60));
      Line sLine = new Line(centerX, centerY, secondX, secondY);
      sLine.setStroke(Color.RED);

      // Draw minute hand
      double mLength = clockRadius * 0.65;
      double xMinute = centerX + mLength * Math.sin(minute * (2 * Math.PI / 60));
      double minuteY = centerY - mLength * Math.cos(minute * (2 * Math.PI / 60));
      Line mLine = new Line(centerX, centerY, xMinute, minuteY);
      mLine.setStroke(Color.BLUE);

      // Draw hour hand
      double hLength = clockRadius * 0.5;
      double hourX = centerX + hLength * Math.sin((hour % 12 + minute / 60.0) * (2 * Math.PI / 12));
      double hourY = centerY - hLength * Math.cos((hour % 12 + minute / 60.0) * (2 * Math.PI / 12));
      Line hLine = new Line(centerX, centerY, hourX, hourY);
      hLine.setStroke(Color.GREEN);

      this.getChildren().clear();
      this.getChildren().addAll(circle, t1, t2, t3, t4, sLine, mLine, hLine);
    }

    public void stopClock() {
      this.clockThread.stopClock();
      this.clockThread.interrupt();
      //      this.clockThread = new ClockThread(sleepTime,this);
      //      this.clockThread.start();
    }
  }
}
