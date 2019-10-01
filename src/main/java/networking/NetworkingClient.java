package networking;

import application.Storage;
import client.Client;
import client.DataSync;
import client.TextData;
import client.UpdateRequest;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import optics.light.Ray;
import optics.objects.OpticalRectangle;
import serialize.Deserialize;
import serialize.Serializable;

import java.util.Random;

public class NetworkingClient {
  private static Client client;

  public static void init(Pane parent) {
    new Thread(() -> {
      client = new Client(successResponse -> {
        System.out.println("Received: " + successResponse.getMessage());
        String message = successResponse.getMessage();
        if (message.contains("objectsUpdate")) {
          if (message.contains("type")) {
            //  Update
            UpdateRequest data = new Gson().fromJson(message, UpdateRequest.class);
            System.out.println(data.getType());
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
                  System.out.println(serializable);
                  if (serializable instanceof OpticalRectangle) {
                    Storage.replaceOpticalRectangle((OpticalRectangle) serializable, data.getIndex() + 1);
                  } else if (serializable instanceof Ray) {
                    Storage.addRay((Ray) serializable);
                  }
                  Storage.reRenderAll();
                });
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
      }, () -> {
        // On connect
        client.send(new TextData("setId", System.getProperty("user.name") + new Random().nextInt()));
        client.send(new TextData("createRoom", "test"));
        client.send(new TextData("setRoom", "test"));
        return null;
      });
      client.init();
    }).start();
  }

  public static void addObject(Serializable serializable) {
    client.send(new TextData("updateObjects", new UpdateRequest("create", serializable.serialize(), 0).toString()));
  }

  public static void removeObject(int index) {
    client.send(new TextData("updateObjects", new UpdateRequest("delete", "", index - 1).toString()));
  }

  public static void updateObject(Serializable serializable, int index) {
    client.send(new TextData("updateObjects", new UpdateRequest("update", serializable.serialize(), index - 1)
            .toString()));
  }

  public static void shutdown() {
    client.close();
  }
}
