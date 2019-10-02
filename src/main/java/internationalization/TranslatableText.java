package internationalization;

import javafx.application.Platform;
import javafx.scene.control.Labeled;
import javafx.util.Callback;

import java.io.IOException;
import java.util.*;

public class TranslatableText {
  private Labeled labeled;
  private String englishText;
  private Map<Locale, String> cache = new HashMap<>();

  public TranslatableText(Labeled label, String englishText) {
    this.labeled = label;
    this.labeled.setWrapText(true);
    this.setEnglishText(englishText);
  }

  public TranslatableText(Labeled label) {
    this(label, label.getText());
  }

  public String getEnglishText() {
    return englishText;
  }

  public Labeled getLabeled() {
    return labeled;
  }

  private void setEnglishText(String englishText) {
    cache.put(new Locale("en", "US"), englishText);
    cache.put(new Locale("en"), englishText);
    this.englishText = englishText;
  }

  public void translate(Locale locale) {
    translate(locale, null, null);
  }

  public void translate(Locale locale, ResourceBundle resourceBundle) {
    translate(locale, null, resourceBundle);
  }

  public void translate(Locale locale, Callback<String, Void> callback) {
    translate(locale, callback, null);
  }


  public void translate(Locale locale, Callback<String, Void> callback, ResourceBundle resourceBundle) {
    if (cache.containsKey(locale)) {
      System.out.println("Cache hit");
      this.labeled.setText(cache.get(locale));
      return;
    }
    if (resourceBundle != null) {
      String id = getEnglishText().replaceAll(" ", "-").replaceAll(":", "");
      if (id.equals("")) return;
      try {
        String result = resourceBundle.getString(id);
        cache.put(locale, result);
        this.labeled.setText(result);
        if (callback != null) callback.call(result);
        return;
      } catch (MissingResourceException e) {
        //        Not found in resource bundle, continue
      }
    }
    new Thread(() -> {
      try {
        String result = TranslatorAPI.translate(this.englishText, "en", locale.getLanguage());
        if (result.contains("html")) {
          if (result.contains("block")) {
            System.out.println("Rate limited by google");
            callback.call("Error: Rate limited");
            return;
          }
          //  Something is wrong
          System.out.println(result);
        }
        cache.put(locale, result);
        if (callback != null) callback.call(result);
        Platform.runLater(() -> this.labeled.setText(result));
      } catch (IOException e) {
        System.out.println("Translation error");
        e.printStackTrace();
      }
    }).start();
  }
}
