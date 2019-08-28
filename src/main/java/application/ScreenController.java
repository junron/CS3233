package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;

public class ScreenController {
  private static HashMap<String, Pane> screenMap = new HashMap<>();
  private static Scene main;
  private static Class loadClass;

  public static void initialize(Scene main, Class<? extends Main> aClass) {
    ScreenController.main = main;
    ScreenController.loadClass = aClass;
  }

  public static void addScreen(String name, Pane pane) {
    screenMap.put(name, pane);
  }

  public static void addScreen(String name) throws IOException {
    addScreen(name, FXMLLoader.load(loadClass.getResource("/" + name + ".fxml")));
  }

  public static void removeScreen(String name) {
    screenMap.remove(name);
  }

  public static void activate(String name) {
    main.setRoot(screenMap.get(name));
  }
}