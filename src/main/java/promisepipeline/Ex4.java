package promisepipeline;

import java.util.concurrent.CompletableFuture;

public class Ex4 {
  public static void main(String[] args) {
    // Exceptions (limited to Unchecked exceptions) create a "parallel pipeline" alongside
    // the main one. The next processing element that can handle exceptions will be invoked
    // for recovery but this need not be the next item down the pipe.
    CompletableFuture.supplyAsync(() -> "base")
        .thenApplyAsync(v -> {
          if (Math.random() > 0.5) throw new RuntimeException("Bad filename!");
          else return v + "3.txt";
        })
        .thenApplyAsync(v -> v.toUpperCase()) // only called if not an exception
        .handleAsync((success, failure) -> {
          if (success != null) return "It worked, with the value " + success;
          else return "Bah, it failed, with exception " + failure.getMessage();
        }) // called with success or failure (hope it returns normally, but doesn't have to!)
        .thenApplyAsync(v -> v.toUpperCase())
        .thenAccept(v -> System.out.println(v))
        .join();
    System.out.println("all done");

  }
}
