package promisepipeline;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Ex5 {
  public static void delay() {
    try {
      Thread.sleep((int) (Math.random() * 2000) + 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static CompletableFuture<String> getFileContents(String filename) {
    // TOTALLY FAKE, for illustration of CompletableFuture concepts only :)
    // Create a CompletableFuture for communication with the CompletableFuture Infrastructure:
    CompletableFuture<String> resultCarrier = new CompletableFuture<>();

    // before returning the communication endpoint (resultCarrier) set the background
    // work in motion:
    new Thread(() -> {
      System.out.println("Background/OS thread started");
      delay();
      if (Math.random() > 0.5) {
        // pretend the IO failed:
        resultCarrier.completeExceptionally(new IOException("File " + filename + " is broken!"));
      } else {
        // normal completion
        resultCarrier.complete("This is the contents of file " + filename);
      }
    }).start();

    return resultCarrier; // hand this back to the infrastructure so the real result can be
    // communicated when ready
  }

  public static void main(String[] args) {
    // File IO operations are essentially universally blocking (some mainframe OS might not
    // have this limitation, but I believe it applies to all of win/mac/linux/solaris.
    // However, in many / most systems the blocking can be handled by the OS with some kind
    // of callback mechanism. (network IO can be truly non-blocking)
    // IF you have a library that integrates CompletableFuture with OS threads, or with a pool
    // of worker threads, you can create non-blocking effects. As of Java 11, HttpClient is
    // the only such implementation.
    // Here, I illustrate how to "patch" an API into the CompletableFuture mechanism, simulating
    // an OS / Pool thread using an explicitly created thread:
    CompletableFuture.supplyAsync(() -> "filename.txt")
        .thenComposeAsync(fn -> getFileContents(fn))
        .handleAsync((s, f) -> {
          if (f != null) {
            return "The file could not be opened, reporting the error: " + f.getMessage();
          } else return s;
        })
        .thenAcceptAsync(s -> System.out.println(s))
        .join();

  }
}
