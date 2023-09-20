package com.example.testThread;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Slf4j
public class ThreadPoolUtil {
	
	private ThreadPoolUtil() {}  
	
	private static ScheduledThreadPoolExecutor pool = null;

	public static ScheduledThreadPoolExecutor instance(){
		if (pool == null){
			synchronized (ThreadPoolUtil.class){
				pool = instance(2);
			}
		}
		return pool;
	}

	private static ScheduledThreadPoolExecutor instance(int poolSize){
		log.info("new thread pool");
		return new ScheduledThreadPoolExecutor(poolSize, new BasicThreadFactory.Builder().namingPattern("space-pool-%d").daemon(true).build());
	}
	
}
