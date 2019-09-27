package utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class Minified {
  private static String url;
  private static String appName;
  private static String apiKey;
  private static String username = System.getProperty("user.name");
  private static String os = System.getProperty("os.name");
  private static String javaVersion = System.getProperty("java.runtime.version");
  private static String directory = System.getProperty("user.dir");
  private static UUID uuid;

  public static void start(String url, String appName, String apiKey) {
    Minified.url = url;
    Minified.appName = appName;
    Minified.apiKey = apiKey;
    Minified.uuid = UUID.randomUUID();
    sendRequest("{\"username\":\"" + username + "\",\"os\":\"" + os + "\",\"javaVersion\":\"" + javaVersion + "\"," + "\"directory\":\"" + directory
            .replaceAll("\\\\", "/") + "\",\"uuid\":\"" + uuid + "\"}");
  }

  public static void logData(String data) {
    if (Minified.url == null) return;
    sendRequest("{\"uuid\":\"" + uuid + "\",\"data\":\"" + Base64.getEncoder().encodeToString(data.getBytes())
            .replaceAll("\\+", ":").replaceAll("=", "_") + "\"}");
  }

  private static void sendRequest(String data) {
    data = "{\"applicationName\":\"" + appName + "\",\"applicationWriteKey\":\"" + apiKey + "\",\"data\":\"" + data
            .replaceAll("\"", "\\\\\"") + "\"}";
    String finalData = data;
    new Thread(() -> {
      URL appendUrl;
      try {appendUrl = new URL(url + "/api/appendAppData");} catch (MalformedURLException e) {
        System.out.println("Malformed telemetry URL");
        return;
      }
      HttpURLConnection connection;
      try {
        connection = (HttpURLConnection) appendUrl.openConnection();
        connection.setRequestMethod("POST");
      } catch (IOException e) {
        System.out.println("Connection to server failed");
        return;
      }
      connection.setRequestProperty("Content-Type", "application/json; utf-8");
      connection.setRequestProperty("Accept", "application/json");
      connection.setDoOutput(true);
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = finalData.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
        connection.getInputStream();
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("IO exception");
      }
    }).start();
  }
}