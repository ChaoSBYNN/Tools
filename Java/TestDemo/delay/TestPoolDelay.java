package com.example.delay;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2023/1/13 11:49
 */
@Slf4j
public class TestPoolDelay {

    public static void main(String[] args) {
        for (int i = 0 ; i <= 20; i++) {

            ThreadPoolUtil.instance(4).schedule(() -> {
                try {
                    System.out.println("11111");
                } catch (Exception e) {
                    log.error("执行 StudentAttendanceRemoteEvent发生异常", e);
                }
            }, 2, TimeUnit.SECONDS);
        }
    }

}
