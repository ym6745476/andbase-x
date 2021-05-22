
package com.andbase.library.asynctask;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Process;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 线程工厂
 */

public class AbThreadFactory {

	/** 任务执行器. */
	public static Executor executor = null;

    /**
     * 获取执行器.
     * @return the executor service
     */
    public static Executor getExecutorService() {
        if (executor == null) {
            executor = Executors.newCachedThreadPool();
        }
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        return executor;
    }

}
