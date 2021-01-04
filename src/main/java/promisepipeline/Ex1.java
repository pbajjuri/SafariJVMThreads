package promisepipeline;

import java.util.concurrent.CompletableFuture;

public class Ex1 {
  public static void main(String[] args) throws Throwable {
    // most basic pipeline example:
    // create the "head" of a pipeline
    var cfv = CompletableFuture.supplyAsync(() -> "base") // string "base" is passed into the pipe
        // thenApply is really a "map" operation, changes the data to upper case
        // "async" suffix invites use of a different thread at each step
        .thenApplyAsync(v -> v.toUpperCase())
        // convert to a (fake) filename
        .thenApplyAsync(v -> v + "3.txt")
        // then print the result
        // thenAccept is like the forEach or ifPresent of Stream or Optional
        // NOTE Completable future handles a *single* data item NOT a sequence
        // (it also can't really be "empty")
        .thenAccept(v -> System.out.println("Computed filename: " + v));
    // Note that this message precedes the one about the filename...
    System.out.println("Pipeline prepared...");
    // if you leave this out, the pipeline will be killed before it finishes (usually)
    // the threads used are daemon threads!
    Thread.sleep(1000);
    System.out.println("main exiting...");

    // Better way to do this is to use "join". Must keep the variable representing the
    // completable future for this to work. (to observe join keep the "var cfv =" and
    // comment out the previous sleep/print, then uncomment this)
    cfv.join();
    System.out.println("completable future completed, main exiting...");
  }
}
