package runnable;

import java.util.concurrent.Executor;

public class Runner {

    static Executor executor;

    public static void main(String[] args) {
        executor = new ThreadPerTaskExecutor();
        executor.execute(new SystemStatusReader());
    }

    static class ThreadPerTaskExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }
}
