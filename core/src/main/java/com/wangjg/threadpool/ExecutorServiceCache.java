package com.wangjg.threadpool;

import com.google.common.collect.Maps;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存ExecutorService实例的类，确保每个ID对应唯一的ExecutorService实例。
 * 使用ConcurrentHashMap来保证线程安全，并结合WeakReference防止内存泄漏。
 */
@Slf4j
public class ExecutorServiceCache {

  // 使用ConcurrentHashMap来保证线程安全，并结合WeakReference防止内存泄漏
  private static final Map<String, WeakReference<ExecutorService>> executorServiceMap =
      Maps.newConcurrentMap();

  /** 私有构造函数，防止实例化此对象。 */
  private ExecutorServiceCache() {
    throw new IllegalArgumentException("不应该实例化此对象");
  }

  /**
   * 根据id获取或创建全局唯一的ExecutorService。
   *
   * @param id 线程池标识符
   * @return 对应于给定id的ExecutorService
   */
  public ExecutorService getSingleExecutorServiceByID(String id) {
    if (id == null || id.isEmpty()) {
      throw new IllegalArgumentException("ID cannot be null or empty");
    }

    // 尝试从缓存中获取弱引用
    WeakReference<ExecutorService> weakRef = executorServiceMap.get(id);
    ExecutorService executorService = weakRef != null ? weakRef.get() : null;

    // 如果找不到有效的线程池，则创建一个新的
    if (executorService == null || executorService.isShutdown()) {
      synchronized (executorServiceMap) {
        // 再次检查以避免重复创建
        weakRef = executorServiceMap.get(id);
        executorService = weakRef != null ? weakRef.get() : null;

        if (executorService == null || executorService.isShutdown()) {
          executorService = createNewSingleThreadExecutor();
          // 使用WeakReference存储线程池
          executorServiceMap.put(id, new WeakReference<>(executorService));
        }
      }
    }

    return executorService;
  }

  /**
   * 创建一个新的单线程ExecutorService，并使用AutoShutdownExecutorService进行包装。
   *
   * @return 新创建的ExecutorService实例
   */
  private ExecutorService createNewSingleThreadExecutor() {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    return AutoShutdownExecutorService.wrapExecutorService(executorService);
  }
}
