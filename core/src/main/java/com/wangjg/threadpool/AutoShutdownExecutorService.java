package com.wangjg.threadpool;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;

/** 自动关闭的ExecutorService实现类。 该类包装了一个ListeningExecutorService，并在所有提交的任务完成后自动关闭。 */
public class AutoShutdownExecutorService implements ExecutorService {
  private final ListeningExecutorService executorService; // 内部使用的ListeningExecutorService实例
  private final List<ListenableFuture<?>> futures = new ArrayList<>(); // 存储所有提交的任务的Future
  private final ReentrantLock lock = new ReentrantLock(); // 用于同步的锁
  private boolean shutdownCalled = false; // 标记shutdown方法是否被调用过

  /**
   * 包装一个ExecutorService实例，返回一个AutoShutdownExecutorService实例。
   * 如果传入的ExecutorService已经是AutoShutdownExecutorService的实例，则直接返回。
   *
   * @param executorService 需要包装的ExecutorService实例
   * @return 包装后的AutoShutdownExecutorService实例
   */
  public static ExecutorService wrapExecutorService(ExecutorService executorService) {
    if (executorService instanceof AutoShutdownExecutorService) {
      return executorService;
    }

    return new AutoShutdownExecutorService(executorService);
  }

  /**
   * 私有构造函数，用于创建AutoShutdownExecutorService实例。 使用传入的ExecutorService创建一个ListeningExecutorService实例。
   *
   * @param executorService 需要包装的ExecutorService实例
   */
  private AutoShutdownExecutorService(ExecutorService executorService) {
    this.executorService = MoreExecutors.listeningDecorator(executorService);
  }

  /** 关闭ExecutorService，如果尚未关闭。 该方法是线程安全的。 */
  @Override
  public void shutdown() {
    lock.lock();
    try {
      if (!shutdownCalled) {
        shutdownCalled = true;
        executorService.shutdown();
        System.out.println("ExecutorService is shutting down.");
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * 尝试立即关闭ExecutorService，并返回等待执行的任务列表。
   *
   * @return 等待执行的任务列表
   */
  @NotNull
  @Override
  public List<Runnable> shutdownNow() {
    return executorService.shutdownNow();
  }

  /**
   * 判断ExecutorService是否已经关闭。
   *
   * @return 如果ExecutorService已经关闭，返回true；否则返回false
   */
  @Override
  public boolean isShutdown() {
    return executorService.isShutdown();
  }

  /**
   * 判断ExecutorService是否已经终止。
   *
   * @return 如果ExecutorService已经终止，返回true；否则返回false
   */
  @Override
  public boolean isTerminated() {
    return executorService.isTerminated();
  }

  /**
   * 等待ExecutorService终止，最多等待指定的时间。
   *
   * @param timeout 等待的最大时间
   * @param unit 时间单位
   * @return 如果ExecutorService在指定时间内终止，返回true；否则返回false
   * @throws InterruptedException 如果当前线程在等待时被中断
   */
  @Override
  public boolean awaitTermination(long timeout, @NotNull TimeUnit unit)
      throws InterruptedException {
    return executorService.awaitTermination(timeout, unit);
  }

  /**
   * 提交一个Callable任务，并返回一个Future对象。
   *
   * @param task 需要提交的Callable任务
   * @param <T> 任务的返回类型
   * @return 表示任务执行结果的Future对象
   */
  @NotNull
  @Override
  public <T> Future<T> submit(@NotNull Callable<T> task) {
    ListenableFuture<T> future = executorService.submit(task);
    manageFuture(future);
    return future;
  }

  /**
   * 提交一个Runnable任务，并返回一个Future对象。
   *
   * @param task 需要提交的Runnable任务
   * @param result 任务完成后的返回结果
   * @param <T> 任务的返回类型
   * @return 表示任务执行结果的Future对象
   */
  @NotNull
  @Override
  public <T> Future<T> submit(@NotNull Runnable task, T result) {
    ListenableFuture<T> future = executorService.submit(task, result);
    manageFuture(future);
    return future;
  }

  /**
   * 提交一个Runnable任务，并返回一个Future对象。
   *
   * @param task 需要提交的Runnable任务
   * @return 表示任务执行结果的Future对象
   */
  @NotNull
  @Override
  public Future<?> submit(@NotNull Runnable task) {
    ListenableFuture<?> future = executorService.submit(task);
    manageFuture(future);
    return future;
  }

  /**
   * 执行一个Runnable任务。 使用submit方法代替execute方法以便获取Future对象。
   *
   * @param command 需要执行的Runnable任务
   */
  @Override
  public void execute(@NotNull Runnable command) {
    ListenableFuture<?> future = executorService.submit(command);
    manageFuture(future);
  }

  /**
   * 管理提交的任务的Future对象。 将Future对象添加到futures列表中，并添加一个监听器，当所有任务完成后自动关闭ExecutorService。
   *
   * @param future 需要管理的Future对象
   */
  private void manageFuture(ListenableFuture<?> future) {
    lock.lock();
    try {
      futures.add(future);
      future.addListener(
          () -> {
            if (isAllDone()) {
              shutdown();
            }
          },
          executorService);
    } finally {
      lock.unlock();
    }
  }

  /**
   * 判断所有提交的任务是否都已经完成。
   *
   * @return 如果所有任务都已经完成，返回true；否则返回false
   */
  private boolean isAllDone() {
    for (Future<?> future : futures) {
      if (!future.isDone()) {
        return false;
      }
    }
    return true;
  }

  // 代理其他未实现的方法到内部的executorService

  /**
   * 提交一组Callable任务，并返回一个Future列表。
   *
   * @param tasks 需要提交的Callable任务列表
   * @param <T> 任务的返回类型
   * @return 表示任务执行结果的Future列表
   * @throws InterruptedException 如果当前线程在等待时被中断
   */
  @NotNull
  @Override
  public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks)
      throws InterruptedException {
    return executorService.invokeAll(tasks);
  }

  /**
   * 提交一组Callable任务，并返回一个Future列表。 最多等待指定的时间。
   *
   * @param tasks 需要提交的Callable任务列表
   * @param timeout 等待的最大时间
   * @param unit 时间单位
   * @param <T> 任务的返回类型
   * @return 表示任务执行结果的Future列表
   * @throws InterruptedException 如果当前线程在等待时被中断
   */
  @NotNull
  @Override
  public <T> List<Future<T>> invokeAll(
      @NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit)
      throws InterruptedException {
    return executorService.invokeAll(tasks, timeout, unit);
  }

  /**
   * 提交一组Callable任务，并返回其中一个任务的结果。
   *
   * @param tasks 需要提交的Callable任务列表
   * @param <T> 任务的返回类型
   * @return 其中一个任务的结果
   * @throws InterruptedException 如果当前线程在等待时被中断
   * @throws ExecutionException 如果任务执行过程中发生异常
   */
  @NotNull
  @Override
  public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks)
      throws InterruptedException, ExecutionException {
    return executorService.invokeAny(tasks);
  }

  /**
   * 提交一组Callable任务，并返回其中一个任务的结果。 最多等待指定的时间。
   *
   * @param tasks 需要提交的Callable任务列表
   * @param timeout 等待的最大时间
   * @param unit 时间单位
   * @param <T> 任务的返回类型
   * @return 其中一个任务的结果
   * @throws InterruptedException 如果当前线程在等待时被中断
   * @throws ExecutionException 如果任务执行过程中发生异常
   * @throws TimeoutException 如果在指定时间内没有任务完成
   */
  @Override
  public <T> T invokeAny(
      @NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return executorService.invokeAny(tasks, timeout, unit);
  }
}
