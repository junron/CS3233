import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Test {
  public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
    ThreadedRequestService.startService();
    HttpRequest httpRequest = new HttpRequest("https://appventure.nushigh.edu.sg", HttpMethod.GET);
    Future future = ThreadedRequestService.newConnection(httpRequest.connect());
    HttpURLConnection conn = (HttpURLConnection) future.get();
    System.out.println(HttpResponse.readRaw(conn));
    ThreadedRequestService.shutdown();
  }
}
