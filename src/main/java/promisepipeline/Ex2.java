package promisepipeline;

import java.util.concurrent.CompletableFuture;

public class Ex2 {
  public static void main(String[] args) throws Throwable {
    // Let's look at the threads, code is functionally identical to Ex1:
    // NOTE for simple situations, you probably will not see more than a single thread
    // from the pool ever getting used.
    var cfv =
        CompletableFuture.supplyAsync(() -> {
          System.out.println("Running supply in: " + Thread.currentThread().getName());
          return "base";
        })
        .thenApplyAsync(v -> {
          System.out.println("Running apply async in: " + Thread.currentThread().getName());
          return v.toUpperCase();
        })
        .thenApplyAsync(v -> {
          System.out.println("Running apply async in: " + Thread.currentThread().getName());
          return v + "3.txt";
        })
        .thenAccept(v -> {
          System.out.println("Running accept in: " + Thread.currentThread().getName());
          System.out.println("Computed filename: " + v);
        });
    System.out.println("Pipeline prepared...");
    cfv.join();
    System.out.println("completable future completed, main exiting...");
  }
}
