package internationalization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TranslatorAPI {
  public static String sendRequest(String url, String parameters) throws IOException {
    String fullUrl = ((parameters != null) && parameters.length() > 0) ? (url + "?" + parameters) : url;
    HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl).openConnection();
    connection.setRequestMethod("GET");
    //    Set fake user agent so we dont get blocked
    connection
            .setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75" +
                            ".0.3770.100 Safari/537.36");
    connection.setDoOutput(true);

    int status = connection.getResponseCode();

    BufferedReader readStream = new BufferedReader(status > 299 ? new InputStreamReader(connection
            .getErrorStream(), StandardCharsets.UTF_8) : new InputStreamReader(connection
            .getInputStream(), StandardCharsets.UTF_8));
    StringBuilder output = new StringBuilder();
    String outputLine;
    while ((outputLine = readStream.readLine()) != null) {
      output.append(outputLine);
    }
    connection.disconnect();
    return output.toString();
  }

  public static String translate(String source, String sourceLang, String targetLang) throws IOException {
    //    Construct full url for using google translate
    String url = "https://translate.googleapis.com/translate_a/single";
    String params = "client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + URLEncoder
            .encode(source, StandardCharsets.UTF_8);
    String response = sendRequest(url, params);
    return response.substring(4).split("\",\"")[0];
  }

  //  public static void main(String[] args) throws IOException {
  //    String result = translate("Hello, world","en","zh");
  //    System.out.println(result);
  //  }

}

