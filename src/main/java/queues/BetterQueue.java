package queues;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BetterQueue<E> {
  ReentrantLock lock = new ReentrantLock();
  // a key benefit of Reentrant lock is the ability to "wait" and "notify"
  // for more than one reason, and to distinguish those reasons, both
  // when waiting and when notifying.
  // NOTE wait/notify/notifyAll are FINAL methods of java.lang.Object
  // so they cannot be modified for the reentrant lock. Instead,
  // Reentrant lock uses await and signal :)
  // Also note: The conditions do not include "logic" that's *our* problem
  // they're equivalent to communication endpoints¸ so the variable names
  // explain *why/how* the code will use the object.
  Condition notFull = lock.newCondition();
  Condition notEmpty = lock.newCondition();

  private E[] data = (E[])(new Object[10]);
  private int count = 0;

  public void put(E e) throws InterruptedException {
    // NOTE: ReentrantLock can lockInterruptibly, a potentially
    // huge benefit over synchronized (facilitates clean shutdown!)
    // also has lock-with-timeout option
    lock.lock();
    try { // Library features can't do the "magic release" when
      // exiting a block, so we must ENSURE the release
      // "finally" is good for this, but mandates we must use
      // a try block (even though we don't really care about
      // exceptions at this level
      while(count == 10)
        notFull.await(); // As wait, releases lock, and does not use CPU
      data[count++] = e;

      // tell anyone waiting for data that data is now available
      notEmpty.signal();
    } finally {
      lock.unlock();
    }
  }

  public E take() throws InterruptedException {
    lock.lock();
    try {
      while (count == 0)
        notEmpty.await(); // notice, waiting for different reason!
      E rv = data[0];
      System.arraycopy(data, 1, data, 0, --count);
      notFull.signal(); // again: notifying for a distinct reason
      return rv;
    } finally {
      lock.unlock();
    }
  }

  // main method is unchanged...
  public static void main(String[] args) throws Throwable {
    BadQueue<int[]> queue = new BadQueue<>();
//    BlockingQueue<int[]> queue = new ArrayBlockingQueue<>(10);

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
