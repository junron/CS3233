package networking;

import application.ServerTabController;
import application.Storage;
import client.Client;
import client.TextData;
import client.UpdateRequest;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import optics.light.Ray;
import optics.objects.OpticalRectangle;
import serialize.Deserialize;
import serialize.Serializable;

import java.util.ArrayList;
import java.util.Random;

public class NetworkingClient {
  private static Client client;
  private static ServerTabController controller;
  private static boolean connected;


  public static void init(Pane parent, ServerTabController controller) {
    NetworkingClient.controller = controller;
    new Thread(() -> {
      client = new Client(successResponse -> {
        String message = successResponse.getData();
        switch (successResponse.getType()) {
          case "objectUpdate": {
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
            break;
          }
          case "objectsUpdate": {
            ArrayList objects = new Gson().fromJson(message, ArrayList.class);
            Platform.runLater(() -> {
              for (Object object : objects) {
                if (object instanceof String) Deserialize.deserializeAndAdd((String) object, parent);
              }
              Storage.reRenderAll();
            });
            break;
          }
          case "join": {
            controller.addServerMessage("User " + message + " has joined.");
            break;
          }
          case "left": {
            controller.addServerMessage("User " + message + " has left.");
            break;
          }
          case "setRoom": {
            controller.addServerMessage("Joined room " + message);
            connected = true;
            break;
          }
          case "roomCreate": {
            controller.addServerMessage("Room created: " + message);
            connected = true;
            break;
          }
        }
        return null;
      }, failureResponse -> {
        System.out.println("Fail: " + failureResponse.getMessage());
        controller.addServerMessage("Fail: " + failureResponse.getMessage());
        return null;
      }, exception -> {
        showException(exception);
        return null;
      }, () -> {
        // On connect
        sendHandleException(new TextData("setId", System.getProperty("user.name") + ":" + Math
                .abs(new Random().nextInt()), false));
        controller.setServerStatus("Connected");
        return null;
      }, pingStart -> {
        //  Pinger
        controller.setServerLatency("Latency: " + (System.currentTimeMillis() - pingStart) + "ms");
        return null;
      });
      client.reconnect();
    }).start();

  }

  private static void showException(Exception e) {
    e.printStackTrace();
    connected = false;
    switch (e.getClass().getName().substring(e.getClass().getName().lastIndexOf(".") + 1)) {
      case "TimeoutException": {
        controller.setServerStatus("Connection timeout");
        break;
      }
      case "CancellationException": {
        controller.setServerStatus("Connection closed");
        break;
      }
      default:
        controller.setServerStatus(e.getMessage());
    }
  }

  public static void addObject(Serializable serializable) {
    if (!connected) return;
    sendHandleException(new TextData("updateObjects", new UpdateRequest("create", serializable.serialize(), 0)
            .toString()));
  }

  public static void removeObject(String type, int index) {
    if (!connected) return;
    sendHandleException(new TextData("updateObjects", new UpdateRequest("delete", type, index).toString()));
  }

  public static void updateObject(Serializable serializable, int index) {
    if (!connected) return;
    sendHandleException(new TextData("updateObjects", new UpdateRequest("update", serializable.serialize(), index)
            .toString()));
  }

  public static void join(String roomName) {
    sendHandleException(new TextData("setRoom", roomName));
  }

  public static void create(String roomName) {
    sendHandleException(new TextData("createRoom", roomName));
  }

  private static void sendHandleException(TextData data) {
    client.send(data, e -> {
      showException(e);
      return null;
    });
  }

  public static void shutdown() {
    client.close();
  }
}
