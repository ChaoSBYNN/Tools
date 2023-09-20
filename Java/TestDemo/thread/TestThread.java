package com.example.testThread;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import java.util.concurrent.*;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2022/10/31 16:07
 */
@Slf4j
public class TestThread {

    public static void main(String[] args) throws InterruptedException {

        ScheduledThreadPoolExecutor executor = ThreadPoolUtil.instance();
        for (int i = 0; i<=20; i++) {
            executor.execute(TestThread::aaa);
            TimeUnit.SECONDS.sleep(2);
        }

//        ThreadFactory basicThreadFactory = new BasicThreadFactory.Builder()
//                .namingPattern("basicThreadFactory-").build();
//
//        ExecutorService exec = new ThreadPoolExecutor(1, 1,
//                0L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<Runnable>(10),basicThreadFactory );
//        exec.submit(() -> {
//            log.info("--记忆中的颜色是什么颜色---");
//        });

    }

    public static void aaa() {
        TestCount count = new TestCount();
        count.setCount(count.getCount() + 1);
        log.info("--记忆中的颜色是什么颜色--- {}", count.getCount());
    }
}
