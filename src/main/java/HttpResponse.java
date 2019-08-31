import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponse {
  public static Map readJson(HttpURLConnection connection) throws IOException {
    String output = readRaw(connection);
    Gson gson = new Gson();
    return gson.fromJson(output, Map.class);
  }

  public static String readRaw(HttpURLConnection connection) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection
            .getInputStream(), StandardCharsets.UTF_8))) {
      StringBuilder response = new StringBuilder();
      String responseLine;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
      return response.toString();
    }
  }
}
