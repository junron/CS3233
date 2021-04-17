package application;

import internationalization.Translate;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import serialize.Deserialize;
import serialize.FileOps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static application.Storage.*;
import static java.util.Map.entry;

public class GeneralTabController {

  @FXML
  private ComboBox<String> languageSelect;
  @FXML
  private CheckBox showAngles, darkTheme;
  @FXML
  private Button save, load, clearAll;
  @FXML
  private TextField maxInteract;

  private Map<String, String> languageIdMapping = Map
          .ofEntries(entry("English", "en"), entry("Chinese (Simplified)", "zh"), entry("Chinese (Traditional)",
                  "zh" + "-TW"), entry("Malay", "ms"), entry("Tamil", "ta"), entry("French", "fr"), entry("Hindi", 
                  "hi"), entry("Tagalog", "tl"), entry("Spanish", "es"), entry("Korean", "ko"), entry("Japanese", "ja"
          ), entry("German", "de"), entry("Greek", "el"), entry("Telugu", "te"), entry("Russian", "ru"), entry(
                  "Polish", "pl"), entry("Norwegian", "no"));
  private Map<String, Locale> resourceBundlesLanguages = Map
          .ofEntries(entry("en", new Locale("en", "US")), entry("fr", new Locale("fr", "FR")), entry("zh-TW",
                  new Locale("zh-tw", "TW")), entry("zh", new Locale("zh", "CN")),entry("ms",new Locale("ms", "MY")));


  void initialize(Pane parent) {
    // Translation
    Translate translate = new Translate(parent);
    languageSelect.setItems(FXCollections.observableArrayList(languageIdMapping.keySet()));
    languageSelect.setOnAction(action -> {
      String langId = languageIdMapping.get(languageSelect.getSelectionModel().getSelectedItem());
      Locale locale = resourceBundlesLanguages.get(langId);
      if (locale != null) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("languages", locale);
        translate.translateAll(locale, resourceBundle);
      } else {
        translate.translateAll(new Locale(langId));
      }
    });
    save.setOnMouseClicked(event -> {
      ArrayList<Object> allObjects = new ArrayList<>(opticalRectangles);
      allObjects.addAll(rays);
      try {
        FileOps.save(allObjects, (Stage) parent.getScene().getWindow());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    load.setOnMouseClicked(e -> {
      ArrayList<String> data;
      try {
        data = FileOps.load((Stage) parent.getScene().getWindow());
      } catch (IOException ex) {
        ex.printStackTrace();
        return;
      }
      if (data == null) return;
      for (String object : data) {
        Deserialize.deserializeAndAdd(object, parent);
      }
    });
    clearAll.setOnMouseClicked(event -> clearAll());

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

  @FXML
  private void resetOffset() {
    Storage.setOffset(new Point2D(0,0));
  }
}

