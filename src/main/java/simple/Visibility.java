package simple;

public class Visibility {
  private static volatile boolean stop = false; // creates "happens-before"

  public static void main(String[] args) throws Throwable {
    new Thread(() -> {
      System.out.println(Thread.currentThread().getName()
      + " starting...");

      while (! stop)
        ;

      System.out.println(Thread.currentThread().getName()
          + " ending...");
    }).start();
    System.out.println("Worker started...");
    Thread.sleep(1_000);
    stop = true;
    System.out.println("Stop set to true, main exiting...");
  }
}
