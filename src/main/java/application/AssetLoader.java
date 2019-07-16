package application;

import javafx.scene.image.Image;

import java.net.URL;

public class AssetLoader {
  static Image loadImage(String name){
    return new Image(String.valueOf(AssetLoader.class.getResource("/assets/textures/"+name)));
  }
  public static URL loadResource(String name){
    return AssetLoader.class.getResource("/assets/"+name);
  }
}
