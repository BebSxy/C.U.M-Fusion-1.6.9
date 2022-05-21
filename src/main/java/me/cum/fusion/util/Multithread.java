
package me.cum.fusion.util;

import java.util.concurrent.atomic.*;
import java.util.concurrent.*;

public class Multithread
{
    public static final ExecutorService POOL;
    public static final ScheduledExecutorService RUNNABLE_POOL;
    
    public Multithread() {
    }
    
    public Multithread(final Runnable runnable) {
        Multithread.POOL.execute(runnable);
    }
    
    public static ScheduledFuture<?> schedule(final Runnable r, final long initialDelay, final long delay, final TimeUnit unit) {
        return Multithread.RUNNABLE_POOL.scheduleAtFixedRate(r, initialDelay, delay, unit);
    }
    
    public static ScheduledFuture<?> schedule(final Runnable r, final long delay, final TimeUnit unit) {
        return Multithread.RUNNABLE_POOL.schedule(r, delay, unit);
    }
    
    public static void runAsync(final Runnable runnable) {
        Multithread.POOL.execute(runnable);
    }
    
    public static int getTotal() {
        final ThreadPoolExecutor tpe = (ThreadPoolExecutor)Multithread.POOL;
        return tpe.getActiveCount();
    }
    
    public static void stopTask() {
        Multithread.POOL.shutdown();
        Multithread.RUNNABLE_POOL.shutdown();
    }
    
    static {
        POOL = Executors.newFixedThreadPool(100, new ThreadFactory() {
            final AtomicInteger counter = new AtomicInteger(0);
            
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(r, String.format("Thread %s", this.counter.incrementAndGet()));
            }
        });
        RUNNABLE_POOL = Executors.newScheduledThreadPool(10, new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);
            
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(r, "Thread " + this.counter.incrementAndGet());
            }
        });
    }
}
