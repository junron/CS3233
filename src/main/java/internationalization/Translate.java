package internationalization;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


public class Translate {
  private ObservableList<TranslatableText> labels = FXCollections.observableArrayList();

  public Translate(Pane parent, boolean recursive) {
    for (Labeled label : this.getNodes(parent, recursive)) {
      this.labels.add(new TranslatableText(label));
    }
  }

  public Translate(Pane parent) {
    this(parent, true);
  }

  public void generateProperties(Locale locale, String bundleName) throws IOException {
    File resourceBundleFile = new File(bundleName + ".properties");
    if (!resourceBundleFile.exists()) resourceBundleFile.createNewFile();
    File localeBundleFile = new File(bundleName + "_" + locale + ".properties");
    if (!localeBundleFile.exists()) localeBundleFile.createNewFile();
    FileOutputStream out = new FileOutputStream(localeBundleFile);
    Set<String> ids = new HashSet<>();
    for (TranslatableText label : labels) {
      String id = label.getEnglishText().replaceAll(" ", "-").replaceAll(":", "");
      String uniqueId = id;
      int idNo = 1;
      while (!ids.add(uniqueId)) {
        uniqueId = id + "-" + idNo;
        idNo++;
      }
      String finalUniqueId = uniqueId;
      label.translate(locale, result -> {
        try {
          out.write((finalUniqueId + "=" + result + "\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
          System.out.println("File IO Error");
          e.printStackTrace();
        }
        return null;
      });
    }
  }

  public void translateAll(Locale locale, ResourceBundle resourceBundle) {
    AtomicBoolean rateLimited = new AtomicBoolean(false);
    for (TranslatableText label : labels) {
      label.translate(locale, result -> {
        if (result.equals("Error: Rate limited")) {
          if (rateLimited.get()) return null;
          rateLimited.set(true);
          Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Rate limited");
            alert.setContentText("We have been rate limited by Google translate.\nPlease wait a few minutes");
            alert.showAndWait();
          });
        }
        return null;
      }, resourceBundle);
    }
    rateLimited.set(false);
  }

  public void translateAll(Locale locale) {
    translateAll(locale, null);
  }

  private ObservableList<Labeled> getNodes(Parent p, boolean recursive) {
    ObservableList<Node> childrenUnmodifiable = p.getChildrenUnmodifiable();
    ObservableList<Labeled> nodes = FXCollections.observableArrayList();
    for (Node node : childrenUnmodifiable) {
      if (node instanceof Labeled) {
        nodes.add((Labeled) node);
      }
    }
    if (recursive) {
      for (Node node : childrenUnmodifiable.filtered(node -> node instanceof TabPane)) {
        for (Tab tab : ((TabPane) node).getTabs()) {
          nodes.addAll(getNodes((Parent) tab.getContent(), true));
        }
      }
      for (Node node : childrenUnmodifiable.filtered(node -> !(node instanceof TabPane) && node instanceof Parent)) {
        nodes.addAll(getNodes((Parent) node, true));
      }
    }
    return nodes;
  }

}
