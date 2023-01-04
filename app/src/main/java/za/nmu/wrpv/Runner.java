package za.nmu.wrpv;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;


final class Runner<T>{
    private T param;
    private Predicate<T> runWhen;
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    public Runner() {
        runWhen = param -> true;
    }

    public void setParam(T param) {
        this.param = param;
    }

    public void setRunWhen(Predicate<T> runWhen) {
        this.runWhen = runWhen;
    }

    public void runLater(Consumer<T> consumer) {
        executor.submit(() -> {
            try {
                run(consumer);
            } catch (Exception e) {
                Log.e("cuppano", "runLater: ", e);
            }
        });
    }
    private void run(Consumer<T> consumer) throws Exception {
        while (param == null || !runWhen.test(param)) {
            Thread.sleep(1000);
        }
        consumer.accept(param);
    }

    public void stop() {
        executor.shutdown();
    }
}

