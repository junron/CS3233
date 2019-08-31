import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpRequest {
  final private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
          + "Chrome/76.0.3809.132 Safari/537.36";
  private URL url;
  private HttpMethod method;
  private Map<String, String> parameters;
  private String body;
  private boolean json;

  public HttpRequest(URL url, HttpMethod method) {
    this.url = url;
    this.method = method;
  }

  public HttpRequest(String url, HttpMethod method) throws MalformedURLException {
    this(new URL(url), method);
  }

  public void json(Map<String, Object> data) {
    Gson gson = new Gson();
    this.body = gson.toJson(data);
    this.json = true;
    this.method = HttpMethod.POST;
  }

  public HttpURLConnection connect() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
    connection.setRequestMethod(this.method.name());
    connection.setDoOutput(true);
    connection.setRequestProperty("User-Agent", userAgent);
    if (json) {
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Accept", "application/json");
    }
    if (this.body != null) {
      //      Write body
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = this.body.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }
    }
    return connection;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
