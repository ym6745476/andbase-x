package com.andbase.library.asynctask;

import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.concurrent.Executor;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 用andbase线程池
 */

public class AbPoolTask {
	
	/** 单例对象 The http pool. */
	private static AbPoolTask taskPool = null;
	
	/** 线程执行器. */
	public static Executor executor = null;
	
	/**  存放返回的任务结果. */
    private static HashMap<String,Object> result;
	
	/** 执行完成后的消息句柄. */
    private static Handler handler = new Handler() { 
        @Override 
        public void handleMessage(Message msg) { 
        	AbTaskItem item = (AbTaskItem)msg.obj;
			item.getListener().update(result.get(item.toString()));
        	result.remove(item.toString());
        } 
    }; 
    
	
	/**
	 * 构造线程池.
	 */
    private AbPoolTask() {
        result = new HashMap<String,Object>();
		executor = AbThreadFactory.getExecutorService();
    } 
	
	/**
	 * 单例构造图片下载器.
	 *
	 * @return single instance of AbHttpPool
	 */
    public static AbPoolTask getInstance() {
    	if (taskPool == null) {
			taskPool = new AbPoolTask();
        } 
        return taskPool;
    } 
    
    /**
     * 执行任务.
     * @param item the item
     */
    public void execute(final AbTaskItem item) {
		executor.execute(new Runnable() {
    		public void run() {
    			try {
    				//定义了回调
                    if (item.getListener() != null) {
						result.put(item.toString(), item.getListener().get());

                    	//交由UI线程处理 
                        Message msg = handler.obtainMessage(); 
                        msg.obj = item; 
                        handler.sendMessage(msg); 
                    }                              
    			} catch (Exception e) { 
    				e.printStackTrace();
    			}                         
    		}                 
    	});                 
    	
    }
	
}
