package serialize;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public class FileOps {
  public static boolean save(Collection objects, Stage stage) throws IOException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Ray Simulation");
    fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Ray Simulation files", "*.raysim")
    );
    File file = fileChooser.showSaveDialog(stage);
    if(file==null) return false;
    FileOutputStream out = new FileOutputStream(file);
    for(Object object:objects){
      if(!(object instanceof Serializable)) return false;
      out.write(((Serializable) object).serialize());
      out.write('\n');
    }
    return true;
  }
  public static ArrayList<byte[]> load(Stage stage) throws IOException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load Ray Simulation");
    fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Ray Simulation files", "*.raysim")
    );
    File file = fileChooser.showOpenDialog(stage);
    if(file==null) return null;
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
      return null;
    }
    Stream lines = br.lines();
    ArrayList<byte[]> res = new ArrayList<>();
    lines.forEach(line->{
      res.add(line.toString().getBytes());
    });
    return res;
  }
}
