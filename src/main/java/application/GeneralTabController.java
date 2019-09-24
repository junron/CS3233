package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import optics.PreciseLine;
import optics.light.Ray;
import optics.objects.Mirror;
import optics.objects.Refract;
import optics.objects.Wall;
import serialize.FileOps;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static application.Storage.*;

public class GeneralTabController {

  @FXML
  private CheckBox showAngles, darkTheme;
  @FXML
  private Button save, load, clearAll;
  @FXML
  private TextField maxInteract;

  public void initialize(Pane parent, OpticsTabController optics, RayTabController rayController) {
    save.setOnMouseClicked(event -> {
      ArrayList<Object> allObjects = new ArrayList<>();
      allObjects.addAll(opticalRectangles);
      allObjects.addAll(rays);
      try {
        FileOps.save(allObjects, (Stage) parent.getScene().getWindow());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    load.setOnMouseClicked(e -> {
      ArrayList<byte[]> data;
      try {
        data = FileOps.load((Stage) parent.getScene().getWindow());
      } catch (IOException ex) {
        ex.printStackTrace();
        return;
      }
      if (data == null) return;
      for (byte[] object : data) {
        ByteBuffer buffer = ByteBuffer.wrap(object);
        switch (buffer.getChar()) {
          case 'm': {
            Mirror m = new Mirror(0, 0, 0, 0, parent, 0);
            m.deserialize(object);
            optics.addObject(m, parent);
            break;
          }
          case 'w': {
            Wall w = new Wall(0, 0, 0, 0, parent, 0);
            w.deserialize(object);
            optics.addObject(w, parent);
            break;
          }
          case 'e': {
            Refract re = new Refract(0, 0, 0, 0, parent, 0, 1);
            re.deserialize(object);
            optics.addObject(re, parent);
            break;
          }
          case 'r': {
            Ray r = new Ray(new PreciseLine(new Line()), parent);
            r.deserialize(object);
            rayController.createRay(r);
            break;
          }
        }
      }
    });
    clearAll.setOnMouseClicked(event -> {
      Wall border = (Wall) opticalRectangles.get(0);
      parent.getChildren().removeAll(opticalRectangles);
      opticalRectangles.clear();
      for (Ray r : rays) {
        r.destroy();
      }
      rays.clear();
      opticalRectangles.add(border);
      reRenderAll();
    });

    maxInteract.setOnKeyPressed(event -> {
      int maxInts;
      try {
        maxInts = Integer.parseInt(maxInteract.getText());
        if (maxInts < 10) throw new NumberFormatException();
      } catch (NumberFormatException e) {
        maxInteract.setText(String.valueOf(maximumReflectionDepth));
        return;
      }
      maximumReflectionDepth = maxInts;
      reRenderAll();
    });
  }

  @FXML
  private void triggerShowAnglesChange() {
    Storage.showLabels = showAngles.isSelected();
    reRenderAll();
  }

  @FXML
  private void triggerThemeChange() {
    Storage.darkTheme = darkTheme.isSelected();
    if (Storage.darkTheme) {
      parent.getStylesheets().add(getClass().getResource("/css/dark.css").toExternalForm());
    } else {
      parent.getStylesheets().remove(getClass().getResource("/css/dark.css").toExternalForm());
    }
  }
}

