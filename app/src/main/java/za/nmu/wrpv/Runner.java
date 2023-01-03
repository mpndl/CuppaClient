package za.nmu.wrpv;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;


final class Runner<T>{
    private final BlockingQueue<Consumer<T>> consumers = new LinkedBlockingQueue<>();
    private T param;
    private Predicate<T> runWhen;
    private Thread runner;
    public Runner() {
        runWhen = t -> true;
    }

    public void setParam(T param) {
        this.param = param;
    }

    public void setRunWhen(Predicate<T> runWhen) {
        this.runWhen = runWhen;
    }

    public void runLater(Consumer<T> consumer) {
        consumers.add(consumer);
    }
    private void run() {
        try{
            while(true) {
                Consumer<T> consumer =  consumers.take();
                while (!runWhen.test(param)) {
                    Thread.sleep(1000);
                }
                consumer.accept(param);
            }
        } catch (InterruptedException ignored) {}
    }

    public void start() {
        if (runner == null) runner = new Thread(this::run);
        if (!runner.isAlive()) runner.start();
    }

    public void stop() {
        if (runner != null) {
            if (runner.isAlive()) runner.interrupt();
            runner = null;
        }
    }
}

