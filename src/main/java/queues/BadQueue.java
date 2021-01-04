package queues;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BadQueue<E> {
  private E[] data = (E[])(new Object[10]);
  private int count = 0;

  public void put(E e) throws InterruptedException {
    synchronized (this) {
//      while(count == 10) // lock spinning!!! Wastes CPU, also does not release lock
//        ;
//      while(count == 10)
//        Thread.sleep(10); // still spinning, Does NOT release lock
      while(count == 10)
        this.wait(); // NOT spinning, Releases the lock, recovers lock before continuing...
      data[count++] = e;
//      this.notify(); // wakes "one thread" up... no control over which.
      // FAILS with multiple producers/consumer

      this.notifyAll(); // wakes "all" waiting threads
      // architecturally wasteful!!!

      // Use instead "ReentrantLock" which can "direct" notifications (signal).
    }
  }

  public E take() throws InterruptedException {
    synchronized (this) {
      while (count == 0)
        this.wait();
      E rv = data[0];
      System.arraycopy(data, 1, data, 0, --count);
//      this.notify();
      this.notifyAll();
      return rv;
    }
  }

  public static void main(String[] args) throws Throwable {
//    BadQueue<int[]> queue = new BadQueue<>();
    BlockingQueue<int[]> queue = new ArrayBlockingQueue<>(10);

    Thread prod = new Thread(() -> {
      for (int i = 0; i < 10_000; i++) {
        try {
          int[] data = {i, 0};
          if (i < 100) Thread.sleep(1);
          if (i == 5_000) data[0] = -1;
          data[1] = i;
          queue.put(data);
          data = null;
        } catch (InterruptedException ie) {
          System.out.println("Hmm!");
        }
      }
      System.out.println("Producer finished");
    });
    prod.start();
    Thread cons = new Thread(() -> {
      for (int i = 0; i < 10_000; i++) {
        try {
          int[] data = queue.take();
          if (data[0] != i || data[0] != data[1]) {
            System.out.println("**** ERROR at index " + i);
          }
          if (i > 9_900) Thread.sleep(1);
        } catch (InterruptedException ie) {
          System.out.println("Hmm, at consumer!");
        }
      }
    });
    cons.start();
    prod.join();
    cons.join();
    System.out.println("Finished");
  }
}
