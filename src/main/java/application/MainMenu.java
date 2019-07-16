package application;

import com.almasb.fxgl.app.FXGLMenu;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.MenuType;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import translator.TranslatableText;

import java.util.Map;

import static java.util.Map.entry;

public class MainMenu extends FXGLMenu {

  private Map<String, String> languageIdMapping = Map.ofEntries(
          entry("English", "en"),
          entry("Chinese (Simplified)", "zh"),
          entry("Chinese (Traditional)", "zh-TW"),
          entry("Malay", "ms"),
          entry("Tamil", "ta"),
          entry("French", "fr"),
          entry("Hindi", "hi"),
          entry("Tagalog", "tl"),
          entry("Spanish", "es"),
          entry("Korean", "ko"),
          entry("Japanese", "ja")
  );
  private ObservableList<String> stuff = FXCollections.observableArrayList(
          languageIdMapping.keySet()
  );

  public MainMenu(MenuType type) {
    super(type);
    Node menu = createMenuBodyMainMenu();
    Pane menuContent = getMenuContentRoot();
    menu.setLayoutX((FXGL.getAppWidth() >> 1) - 150);
    menu.setLayoutY(-50 + FXGL.getAppHeight() >> 1);
    menuContent.getStylesheets().add(AssetLoader.loadResource("styles/menu.css").toExternalForm());
    menuContent.getChildren().add(menu);
  }

  @Override
  protected Button createActionButton(String name, Runnable action) {
    System.out.println(name);
    return new Button(name);
  }

  @Override
  protected Button createActionButton(StringBinding name, Runnable action) {
    System.out.println(name);
    return new Button(name.get());
  }

  @Override
  protected Node createBackground(double width, double height) {
    Rectangle bg = new Rectangle(width, height);
    bg.setFill(Color.color(1, 1, 1));
    return bg;
  }

  @Override
  protected Node createTitleView(String title) {
    TranslatableText t = new TranslatableText();
    t.setFont(Font.font(36));
    t.setEnglishText(title);
    double textWidth = t.getLayoutBounds().getWidth();
    HBox box = new HBox(t);
    box.setAlignment(Pos.CENTER);
    box.setTranslateX((FXGL.getAppWidth() >> 1) - (textWidth + 30) / 2);
    box.setTranslateY(50);
    return box;
  }

  @NotNull
  @Override
  protected Node createVersionView(@NotNull String version) {
    return new Text();
  }


  @NotNull
  @Override
  protected Node createProfileView(@NotNull String profileName) {
    return new Text();
  }

  private GridPane createMenuBodyMainMenu() {
    Button playButton = new Button("menu.play");
    playButton.setText("Start game!");
    playButton.setOnMouseClicked(e -> fireNewGame());

    ComboBox<String> languageSelect = new ComboBox<>(stuff);
    TranslatableText label = new TranslatableText("Language:");
    languageSelect.setValue("English");
    languageSelect.valueProperty().addListener((observable, oldValue, newValue) -> {
      String langId = languageIdMapping.get(newValue);
      TranslatableText.translateAll(langId);
    });

    GridPane grid = new GridPane();
//    grid.setGridLinesVisible(true);
    grid.setVgap(4);
    grid.setHgap(10);
    grid.setPadding(new Insets(5, 5, 5, 5));

    grid.add(playButton, 0, 0, 2, 1);
    GridPane.setHalignment(playButton, HPos.CENTER);
    grid.add(label, 0, 1);
    grid.add(languageSelect, 1, 1);
    return grid;
  }
}
