package simple;

public class Counting {
  private static long count = 0;

  public static void main(String[] args) throws Throwable {
    Runnable counter = () -> {
      for (int i = 0; i < 10_000; i++) {
        count++;
      }
    };
//    counter.run();
//    counter.run();

    Thread t1 = new Thread(counter);
    Thread t2 = new Thread(counter);
    t1.start();
    t2.start();
//    Thread.sleep(1_000);
    t1.join();
    t2.join();
    System.out.println("count value is " + count);
  }
}
