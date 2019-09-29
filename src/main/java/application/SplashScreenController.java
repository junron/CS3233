package application;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {
  @FXML
  private ProgressBar progressBar;
  @FXML
  private Label status;

  private ArrayList<String> messages = new ArrayList<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //Random messages
    messages.add("Hacking NASA with HTML");
    messages.add("Making NSC memes");
    messages.add("Ditching Eclipse");
    messages.add("Solving p=np");
    messages.add("Catching all Pokemons");
    messages.add("Resolving dependencies");
    messages.add("Reloading OOP");
    messages.add("Extending classes");
    messages.add("Mining Bitcoins");
    Collections.shuffle(messages);
    //Legit messages
    messages.add(0,"Collecting telemetry");
    messages.add(0,"Connecting to servers");
    String[] out = new String[7];
    for(int i = 0; i < 7; i++) {
      out[i] = messages.get(i);
    }
    new Thread(() -> {
      for (int i = 0; i < 7; i++) {
        int finalI = i;
        Platform.runLater(() -> {
          status.setText(out[finalI]);
          progressBar.setProgress((finalI + 1.0) / 7);
        });
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
}
