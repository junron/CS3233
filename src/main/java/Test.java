import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
  static AtomicInteger bucketA = new AtomicInteger(10_000);
  static AtomicInteger bucketB = new AtomicInteger(10_000);
  static AtomicInteger trialNo = new AtomicInteger(1);
  static Random random = new Random();

  static class RunTask implements Runnable {
    private String name;

    RunTask(String name) {
      this.name = name;
    }

    @Override
    public void run() {
      AtomicInteger bucket = name.equals("Daniel") ? bucketA : bucketB;
      AtomicInteger otherBucket = name.equals("Daniel") ? bucketB : bucketA;
      do {
        int num = random.nextInt(otherBucket.get() + 1);
        bucket.addAndGet(num);
        otherBucket.addAndGet(-num);
        System.out.println("<" + name + "> transferring: " + num);
        System.out.println("After trial: " + trialNo.getAndIncrement());
        System.out.println("Number of balls in bucket A: " + bucketA.get());
        System.out.println("Number of balls in bucket B: " + bucketB.get());
      } while (bucket.get() != 20_000 && bucket.get() != 0);
      if (bucket.get() == 20_000) {
        System.out.println("Number of balls in bucket A: " + bucketA.get());
        System.out.println("Number of balls in bucket B: " + bucketB.get());
        System.out.println(this.name + " won");
      }
    }
  }

  public static void main(String[] args) {
    Thread john = new Thread(new RunTask("John"));
    Thread daniel = new Thread(new RunTask("Daniel"));
    john.start();
    daniel.start();
  }
}

