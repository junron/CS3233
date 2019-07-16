package translator;

import java.util.HashMap;
import java.util.Map;

public class TranslateCache {
  private Map<String,String> translationCache;

  TranslateCache(TranslatableText text) {
    translationCache = new HashMap<>();
    translationCache.put("en",text.getText());
  }

  String checkCache(String targetLang){
    return translationCache.get(targetLang);
  }
  public void addToCache(String targetLang, String result){
    this.translationCache.put(targetLang,result);
  }
}
