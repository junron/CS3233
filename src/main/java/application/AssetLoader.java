package application;

import javafx.scene.image.Image;

public class AssetLoader {
  public static Image loadImage(String name){
    return new Image(String.valueOf(AssetLoader.class.getResource("/assets/textures/"+name)));
  }
}
