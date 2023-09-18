package com.example.delay;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ThreadPoolUtil {

	private ThreadPoolUtil() {}

	private static volatile ScheduledThreadPoolExecutor pool = null;

	public static ScheduledThreadPoolExecutor instance(){
		if (pool == null){
			synchronized (ThreadPoolUtil.class){
				if(pool == null){
					pool = create(20);
				}
			}
		}
		return pool;
	}

	public static ScheduledThreadPoolExecutor instance(int poolSize){
		if (pool == null){
			synchronized (ThreadPoolUtil.class){
				if(pool == null){
					pool = create(poolSize);
				}
			}
		}
		return pool;
	}

	public static ScheduledThreadPoolExecutor create(int poolSize){
		return new ScheduledThreadPoolExecutor(poolSize, new BasicThreadFactory.Builder().namingPattern("space-pool-%d").daemon(true).build());
	}


}
