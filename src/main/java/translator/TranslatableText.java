package translator;

import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TranslatableText extends Text {
  public static List<TranslatableText> allText = new ArrayList<>();
  private String englishText;
  private TranslateCache cache;

  public TranslatableText(String englishText) {
    super(englishText);
    this.englishText = englishText;
    this.cache = new TranslateCache(this);
    allText.add(this);
  }

  public TranslatableText() {
    super();
    this.cache = new TranslateCache(this);
    allText.add(this);
  }

  public void setEnglishText(String text){
    super.setText(text);
    cache.addToCache("en",text);
    this.englishText = text;
  }

  public void translate(String targetLang) {
//    Check cache before using google translate
    String cacheResult = cache.checkCache(targetLang);
    if (cacheResult != null) {
      this.setText(cacheResult);
      return;
    }
    try {
      String result = TranslatorAPI.translate(this.englishText, "en", targetLang);
      cache.addToCache(targetLang, result);
      this.setText(result);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void translateAll(String targetLang) {
    for (TranslatableText text:allText) {
      text.translate(targetLang);
    }
  }
}
