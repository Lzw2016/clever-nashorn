package org.clever.nashorn.test;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.HttpUtils;
import org.clever.nashorn.websocket.utils.ThreadPoolUtils;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/28 10:43 <br/>
 */
@Slf4j
public class ThreadPoolExecutorTest {

    public static class TestThread extends Thread {
        public static long count = 0;

        @Override
        public void run() {
            try {
                while (true) {
                    count++;
                }
            } catch (Throwable e) {
                log.error("catch", e);
            }
        }
    }

    @Test
    public void t1() throws InterruptedException {
        ThreadPoolExecutor threadPool = ThreadPoolUtils.getThreadPoolForTask("123");
        threadPool.execute(() -> {
            for (int i = 1; i < 1000; i++) {
                HttpUtils.getInner().getStr("https://www.baidu.com/");
                log.info("-----> {}", i);
            }
        });
        Thread.sleep(1000);
        threadPool.shutdownNow();
        log.info("-----> shutdownNow!");
        Thread.sleep(5000);
        log.info("-----> OK!");
    }

    @Test
    public void t2() throws InterruptedException {
        Thread thread = new TestThread();
        thread.start();
        thread.interrupt();
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            log.info("-----> count={}", TestThread.count);
            // thread.stop();
        }
        log.info("-----> OK!");
    }

    @Test
    public void t3() throws InterruptedException {
        ThreadPoolExecutor threadPool = ThreadPoolUtils.getThreadPoolForTask("123");
        threadPool.allowCoreThreadTimeOut(true);
        threadPool.execute(new TestThread());
        Thread.sleep(10);
        threadPool.shutdownNow();
        log.info("-----> shutdownNow!");
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            log.info("-----> count={}", TestThread.count);
        }
        Thread.sleep(5000);
        log.info("-----> OK!");
    }

    // ------------------------------------------------------------------------------------------------------------

    public static class TestThread2 extends Thread {
        private final long count;

        public TestThread2(long count) {
            this.count = count;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    log.info("-----> count={}", count);
                    Thread.sleep(1000);
                }
            } catch (Throwable e) {
                // log.error("catch", e);
            }
        }
    }

    @Test
    public void t4() throws InterruptedException {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                2,                          // 核心线程数，即使空闲也仍保留在池中的线程数
                8,                      // 最大线程数
                30, TimeUnit.SECONDS,      // 保持激活时间，当线程数大于核心数时，这是多余的空闲线程在终止之前等待新任务的最大时间
                new ArrayBlockingQueue<>(32),   // 当线程池的任务缓存队列容量
                new ThreadPoolExecutor.AbortPolicy()    // 当线程池的任务缓存队列已满，并且线程池中的线程数目达到最大线程数，如果还有任务到来就会采取任务拒绝策略
        );
        threadPool.allowCoreThreadTimeOut(true);
        threadPool.execute(new TestThread2(1));
        Thread.sleep(10);
        threadPool.execute(new TestThread2(2));
        Thread.sleep(10);
        threadPool.execute(new TestThread2(3));
        // -----------------------------------------------------------------
        Thread.sleep(5000);
        threadPool.shutdownNow();
        log.info("-----> shutdownNow!");
        Thread.sleep(5000);
        log.info("-----> OK!");
    }


//    public static void main(String[] args) {
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        ExecutorService pool = Executors.newFixedThreadPool(1);
//        AtomicInteger counter = new AtomicInteger();
//        pool.execute(() -> {
//            while (true) {
//                try {
//                    counter.incrementAndGet();
//                } catch (Exception e) {
//
//                }
//            }
//        });
//        ExecutorService controlPool = Executors.newFixedThreadPool(1);
//        controlPool.execute(() -> {
//            while (true) {
//                Scanner input = new Scanner(System.in);
//                System.out.print("enter op: ");
//                String opStr = input.next();
//                if (opStr.equals("k")) {
//                    pool.shutdownNow();
//                    System.out.println("pool.isShutdown");
//                }
//                if (opStr.equals("c")) {
//                    System.out.println(pool.isShutdown() + " " + counter.toString());
//                }
//            }
//        });
//        countDownLatch.await();
//    }
}
