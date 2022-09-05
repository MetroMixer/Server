package org.metromixer.server.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadExecutorContainer {

    private final ThreadGroup threadGroup;
    private final AtomicLong threadCounter = new AtomicLong(0);
    private final ExecutorService pool;

    public ThreadExecutorContainer(String name, int maxPool) {
        threadGroup = new ThreadGroup(name);
        pool = new ThreadPoolExecutor(5, maxPool, 1000L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), r -> new Thread(threadGroup, r, name + "-" + threadCounter.incrementAndGet()));
    }

    public void queueThread(Runnable runnable) {
        pool.submit(runnable);
    }

}
