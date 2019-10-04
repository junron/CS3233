package networking;

import application.MainController;
import application.Storage;
import client.Client;
import client.DataSync;
import client.TextData;
import client.UpdateRequest;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import optics.light.Ray;
import optics.objects.OpticalRectangle;
import serialize.Deserialize;
import serialize.Serializable;

import java.util.Random;

public class NetworkingClient {
  private static Client client;

  public static void init(Pane parent, MainController controller) {
    new Thread(() -> {
      client = new Client(successResponse -> {
        System.out.println("Received: " + successResponse.getMessage());
        String message = successResponse.getMessage();
        if (message.contains("objectsUpdate")) {
          if (message.contains("type")) {
            //  Update
            UpdateRequest data = new Gson().fromJson(message, UpdateRequest.class);
            switch (data.getType()) {
              case "create": {
                Platform.runLater(() -> {
                  Deserialize.deserializeAndAdd(data.getData(), parent);
                  Storage.reRenderAll();
                });
                break;
              }
              case "update": {
                Platform.runLater(() -> {
                  Serializable serializable = Deserialize.deserialize(data.getData(), parent);
                  if (serializable instanceof OpticalRectangle) {
                    Storage.updateOpticalRectangle((OpticalRectangle) serializable, data.getIndex() + 1);
                  } else if (serializable instanceof Ray) {
                    Storage.updateRay((Ray) serializable, data.getIndex());
                  }
                  Storage.reRenderAll();
                });
                break;
              }
              case "delete": {
                Platform.runLater(() -> {
                  if (data.getData().equals("r")) {
                    Storage.removeRay(data.getIndex());
                  } else {
                    Storage.removeOptical(data.getIndex() + 1);
                  }
                  Storage.reRenderAll();
                });
                break;
              }
            }
          } else {
            //  Sync
            DataSync data = new Gson().fromJson(message, DataSync.class);
            Platform.runLater(() -> {
              for (String object : data.getObjects()) {
                Deserialize.deserializeAndAdd(object, parent);
              }
              Storage.reRenderAll();
            });
          }
        }
        return null;
      }, failureResponse -> {
        System.out.println("Fail: " + failureResponse.getMessage());
        return null;
      }, exception -> {
        showException(exception);
        return null;
      }, () -> {
        // On connect
        sendHandleException(new TextData("setId", System.getProperty("user.name") + new Random().nextInt()));
        sendHandleException(new TextData("createRoom", "test"));
        sendHandleException(new TextData("setRoom", "test"));
        return null;
      }, pingStart -> {
        //  Pinger
        Platform.runLater(()-> controller.setLatency(System.currentTimeMillis() - pingStart));
        return null;
      });
      client.init();
    }).start();
  }

  private static void showException(Exception e) {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setHeaderText(e.getClass().getName());
      alert.setContentText(e.getMessage());
      alert.showAndWait();
    });
  }

  public static void addObject(Serializable serializable) {
    sendHandleException(new TextData("updateObjects", new UpdateRequest("create", serializable.serialize(), 0).toString()));
  }

  public static void removeObject(String type, int index) {
    sendHandleException(new TextData("updateObjects", new UpdateRequest("delete", type, index).toString()));
  }

  public static void updateObject(Serializable serializable, int index) {
    sendHandleException(new TextData("updateObjects", new UpdateRequest("update", serializable.serialize(), index).toString()));
  }

  private static void sendHandleException(TextData data){
    client.send(data,e->{
      showException(e);
      return null;
    });
  }
  public static void shutdown() {
    client.close();
  }
}
