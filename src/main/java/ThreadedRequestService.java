import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadedRequestService {
  private ExecutorService executors;
  private static ThreadedRequestService service;

  public ThreadedRequestService(int threads) {
    executors = Executors.newFixedThreadPool(threads);
  }

  public static Future newConnection(HttpURLConnection connection) {
    return service.executors.submit(() -> {
      connection.connect();
      System.out.println(connection.getResponseCode());
      return connection;
    });
  }

  public static void startService(int threads) {
    ThreadedRequestService.service = new ThreadedRequestService(threads);
  }

  public static void startService() {
    startService(10);
  }
  public static void shutdown(){
    service.executors.shutdown();
  }
}
