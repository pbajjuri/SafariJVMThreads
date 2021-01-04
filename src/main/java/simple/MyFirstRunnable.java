package simple;

class SimpleRunnable implements Runnable {
  private int i = 0;
  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName()
    + " starting...");
    for (; i < 10_000; i++) {
      System.out.println(Thread.currentThread().getName()
       + " i is " + i);
    }
    System.out.println(Thread.currentThread().getName()
    + " completed...");
  }
}
public class MyFirstRunnable {
  public static void main(String[] args) {
    Runnable r = new SimpleRunnable();
//    r.run();
    Thread t = new Thread(r);
    Thread t2 = new Thread(r);
//    t.run(); NO!!!!
//    t.setDaemon(true);
    t.start();
    t2.start();
    System.out.println(Thread.currentThread().getName() + " exiting...");
  }
}
