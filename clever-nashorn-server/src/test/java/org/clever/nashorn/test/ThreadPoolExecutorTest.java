package org.clever.nashorn.test;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.HttpUtils;
import org.clever.nashorn.websocket.utils.ThreadPoolUtils;
import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/28 10:43 <br/>
 */
@Slf4j
public class ThreadPoolExecutorTest {

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
    public void t2() {
        Thread thread = new Thread(() -> {
            for (int i = 1; i < 1000; i++) {
                HttpUtils.getInner().getStr("https://www.baidu.com/");
                log.info("-----> {}", i);
            }
        });
//        thread.start();
//        thread.stop();
    }
}
