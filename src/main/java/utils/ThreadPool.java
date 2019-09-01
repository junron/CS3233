package utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
  private static ExecutorService executorService;

  public static void initialize(int threads) {
    executorService = Executors.newFixedThreadPool(threads);
  }

  public static ExecutorService getExecutorService() {
    return executorService;
  }
}
