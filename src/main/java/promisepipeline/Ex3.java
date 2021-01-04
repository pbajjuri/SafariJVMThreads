package promisepipeline;

import java.util.concurrent.CompletableFuture;

public class Ex3 {
  public static void main(String[] args) throws Throwable {
    // Look at creating a "fork" and "rejoin" in the pipeline
    // base -> uppercase -\
    //      -> reverse ->-+ -- single output of both names:

    // one completable future that will produce "base"
    var cfv =
        CompletableFuture.supplyAsync(() -> "base");

    // add one "downstream pipe" to it to produce "BASE"
    var cfv1 =
        cfv.thenApplyAsync(v -> v.toUpperCase());

    // add a second pipe to produce "ESAB"
    var cfv2 = cfv.thenApplyAsync(v -> new StringBuilder(v).reverse().toString());

    // now wait for both to complete, and show combined results
    var cfv3 = cfv1.thenAcceptBothAsync(
        cfv2, (a, b) -> System.out.println(a + " and " + b));

    System.out.println("Pipeline prepared...");
    // Note now waiting for cfv3 (final stage)
    cfv3.join();
    System.out.println("completable future completed, main exiting...");
  }
}
