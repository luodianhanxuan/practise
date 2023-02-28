package com.wangjg.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangjg
 * 2019/4/11
 */
public class ThreadPoolUtil {
    private ThreadPoolUtil() {
    }

    // 整个应用程序只创建一个线程池
    private static ExecutorService threadPool = Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors() + 1);

    /**
     * 执行Runnable任务
     */
    public static void execute(Runnable command) {
        if (threadPool.isShutdown()) {
            // 如果线程池关闭，那么就再创建一个线程池
            threadPool = Executors.newCachedThreadPool();
            execute(command);
        } else {
            threadPool.execute(command);
        }
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        if (!threadPool.isShutdown()) {
            threadPool.shutdown();
        }
    }
}
