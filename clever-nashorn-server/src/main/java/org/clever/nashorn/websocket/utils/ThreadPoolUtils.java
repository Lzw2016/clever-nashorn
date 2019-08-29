package org.clever.nashorn.websocket.utils;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.tuples.TupleSeven;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 线程池管理工具
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/28 10:44 <br/>
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
public class ThreadPoolUtils {

    /**
     * 所有的任务 Map<taskId, ThreadPoolExecutor> <br />
     */
    private static final ConcurrentHashMap<String, ThreadPoolExecutor> Thread_Pool_Map = new ConcurrentHashMap<>();
    /**
     * 所有需要Shutdown的ThreadPool
     */
    private static final CopyOnWriteArraySet<ThreadPoolExecutor> Shutdown_Thread_Pool = new CopyOnWriteArraySet<>();

    static {
        // 优雅关闭线程池
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Shutdown_Thread_Pool.addAll(Thread_Pool_Map.values());
                Shutdown_Thread_Pool.forEach(threadPoolExecutor -> {
                    if (!threadPoolExecutor.isShutdown()) {
                        try {
                            threadPoolExecutor.shutdown();
                        } catch (Throwable e) {
                            log.warn("关闭WebSocket任务线程池异常", e);
                        }
                    }
                });
                log.debug("WebSocket任务线程池关闭成功");
            } catch (Throwable e) {
                log.warn("关闭WebSocket任务线程池异常", e);
            }
        }));
    }

    public static TupleSeven<Integer, Integer, Integer, Integer, Integer, Integer, Integer> getThreadPoolInfo() {
        int corePoolSize = 0;
        int maximumPoolSize = 0;
        int poolSize = 0;
        int queueSize = 0;
        int taskCount = 0;
        int activeCount = 0;
        int completedTaskCount = 0;
        for (ThreadPoolExecutor threadPool : Thread_Pool_Map.values()) {
            corePoolSize += threadPool.getCorePoolSize();
            maximumPoolSize += threadPool.getMaximumPoolSize();
            poolSize += threadPool.getPoolSize();
            queueSize += threadPool.getQueue().size();
            taskCount += threadPool.getTaskCount();
            activeCount += threadPool.getActiveCount();
            completedTaskCount += threadPool.getCompletedTaskCount();
        }
        return TupleSeven.creat(corePoolSize, maximumPoolSize, poolSize, queueSize, taskCount, activeCount, completedTaskCount);
    }

    /**
     * 获取任务线程池，如果不存在就创建
     */
    public static ThreadPoolExecutor getThreadPoolForTask(String taskId) {
        ThreadPoolExecutor threadPoolExecutor = Thread_Pool_Map.get(taskId);
        if (threadPoolExecutor != null) {
            if (threadPoolExecutor.isShutdown()) {
                Thread_Pool_Map.remove(taskId);
            } else {
                return threadPoolExecutor;
            }
        }
        threadPoolExecutor = new ThreadPoolExecutor(
                4,                          // 核心线程数，即使空闲也仍保留在池中的线程数
                4,                      // 最大线程数
                10, TimeUnit.SECONDS,      // 保持激活时间，当线程数大于核心数时，这是多余的空闲线程在终止之前等待新任务的最大时间
                new ArrayBlockingQueue<>(32),   // 当线程池的任务缓存队列容量
                new ThreadPoolExecutor.AbortPolicy()    // 当线程池的任务缓存队列已满，并且线程池中的线程数目达到最大线程数，如果还有任务到来就会采取任务拒绝策略
        );
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        Thread_Pool_Map.put(taskId, threadPoolExecutor);
        return threadPoolExecutor;
    }

    public static void shutdownNow(ThreadPoolExecutor threadPool) {
        String taskId = null;
        for (Map.Entry<String, ThreadPoolExecutor> entry : Thread_Pool_Map.entrySet()) {
            if (Objects.equals(entry.getValue(), threadPool)) {
                taskId = entry.getKey();
                Thread_Pool_Map.remove(taskId);
            }
        }
        if (threadPool != null && !threadPool.isShutdown()) {
            Shutdown_Thread_Pool.add(threadPool);
        }
        Set<ThreadPoolExecutor> rmSet = new HashSet<>();
        for (ThreadPoolExecutor shutdownThreadPool : Shutdown_Thread_Pool) {
            if (!shutdownThreadPool.isShutdown()) {
                try {
                    shutdownThreadPool.shutdownNow();
                    log.debug("关闭WebSocket任务线程池成功 | TaskId={}", taskId);
                } catch (Throwable e) {
                    log.error("关闭ThreadPoolExecutor异常", e);
                    continue;
                }
            }
            rmSet.add(shutdownThreadPool);
        }
        Shutdown_Thread_Pool.removeAll(rmSet);
    }

    public static void shutdownNow(String taskId) {
        ThreadPoolExecutor threadPool = Thread_Pool_Map.get(taskId);
        shutdownNow(threadPool);
        Thread_Pool_Map.remove(taskId);
    }
}
