package serialize;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class FileOps {
  public static void save(Collection objects, Stage stage) throws IOException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Ray Simulation");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ray Simulation files", "*.raysim"));
    File file = fileChooser.showSaveDialog(stage);
    if (file == null) return;
    FileOutputStream out = new FileOutputStream(file);
    for (Object object : objects) {
      if (!(object instanceof Serializable)) return;
      out.write(((Serializable) object).serialize().getBytes());
      out.write('\n');
    }
  }

  public static ArrayList<String> load(Stage stage) throws IOException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load Ray Simulation");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ray Simulation files", "*.raysim"));
    File file = fileChooser.showOpenDialog(stage);
    if (file == null) return null;
    return load(file);
  }

  public static ArrayList<String> load(File file) throws IOException {
    byte[] data = Files.readAllBytes(Paths.get(file.getPath()));
    ArrayList<String> res = new ArrayList<>();
    int lastVal = 0;
    int currIndex = 0;
    for (byte b : data) {
      // Each element is denoted by a new line
      if (b == 10) {
        String elem = new String(Arrays.copyOfRange(data, lastVal, currIndex + 1));
        res.add(elem);
        lastVal = currIndex + 1;
      }
      currIndex++;
    }
    return res;
  }
}

