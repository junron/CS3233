package application;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import networking.NetworkingClient;

public class ServerTabController {
  @FXML
  private TextField roomCode;
  private Pane parent;
  @FXML
  private Label latency,status;
  @FXML
  private AnchorPane buttons;

  @FXML
  private TextArea messages;
  public void initialize(Pane parent) {
    this.parent = parent;
  }

  public void connect() {
    NetworkingClient.init(parent, this);
  }

  public void join() {
    if(roomCode.getText().length()==0) return;
    Storage.clearAll(false);
    NetworkingClient.join(roomCode.getText());
  }

  public void create() {
    if(roomCode.getText().length()==0) return;
    Storage.clearAll(false);
    NetworkingClient.create(roomCode.getText());
  }

  public void setServerLatency(String message) {
    Platform.runLater(() -> this.latency.setText(message));
  }
  public void addServerMessage(String message) {
    Platform.runLater(() -> this.messages.setText(messages.getText()+message+"\n"));
  }

  public void setServerStatus(String message) {
    if(message.equals("Connected")){
      buttons.setDisable(false);
    }else{
      buttons.setDisable(true);
    }
    Platform.runLater(() -> this.status.setText("Status: " + message));
  }
}
