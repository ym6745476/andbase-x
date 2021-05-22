package com.andbase.library.asynctask;

import java.util.LinkedList;
import java.util.concurrent.Executor;

import android.os.Handler;
import android.os.Message;

import com.andbase.library.utils.AbLogUtil;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 任务队列
 */
public class AbQueueTask extends Thread {
	
	/** 等待执行的任务. 用 LinkedList增删效率高*/
	private LinkedList<AbTaskItem> taskItemList = null;
  	
  	/** 停止的标记. */
	private boolean quit = false;

	/** 控制标记. */
	private boolean doing = false;
	
	/** 执行完成后的消息句柄. */
    private Handler handler = new Handler() { 
        @Override 
        public void handleMessage(Message msg) { 
        	AbTaskItem item = (AbTaskItem)msg.obj;
			item.getListener().update(item.getResult());
			doing = false;

        } 
    };
	
	/**
	 * 构造执行线程队列.
	 */
    public AbQueueTask() {
    	quit = false;
    	taskItemList = new LinkedList<AbTaskItem>();
    	//从线程池中获取
    	Executor executor  = AbThreadFactory.getExecutorService();
		executor.execute(this);
    }
    
    /**
     * 开始执行任务.
     * @param item 执行单位
     */
    public void execute(AbTaskItem item) { 
         addTaskItem(item); 
    } 
    
    
    /**
     * 开始一个执行任务并清除原来队列.
     * @param item 执行单位
     * @param cancel 清空之前的任务
     */
    public void execute(AbTaskItem item,boolean cancel) { 
	    if(cancel){
	    	 cancel(true);
	    }
    	addTaskItem(item); 
    } 
     
    /**
     * 添加到执行线程队列.
     * @param item 执行单位
     */
    private synchronized void addTaskItem(AbTaskItem item) { 
    	taskItemList.add(item);
    	//添加了执行项就激活本线程 
        this.notify();
        
    }

    /**
	 * 线程运行
	 */
    @Override 
    public void run() { 
        while(!quit) { 
        	try {
        	    while(taskItemList.size() > 0){

                    if(doing){
                        continue;
                    }

					AbTaskItem item = taskItemList.remove(0);
					//定义了回调
				    if (item!=null && item.getListener() != null) {

						doing = true;
						item.getListener().get();

				    	//交由UI线程处理 
				        Message msg = handler.obtainMessage(); 
				        msg.obj = item; 
				        handler.sendMessage(msg); 
				    } 
				    
				    //停止后清空
				    if(quit){
				    	taskItemList.clear();
				    	return;
				    }
        	    }
        	    try {
					//没有执行项时等待 
					synchronized(this) { 
					    this.wait();
					}
				} catch (Exception e) {
					AbLogUtil.e("AbTaskQueue","收到线程中断请求");
					e.printStackTrace();
					//被中断的是退出就结束，否则继续
					if (quit) {
						taskItemList.clear();
	                    return;
	                }
	                continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
        } 
    } 
    
    /**
     * 终止队列释放线程.
     * @param interrupt the may interrupt if running
     */
    public void cancel(boolean interrupt){
		try {
			quit  = true;
			if(interrupt){
				interrupted();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	/**
	 * 获取任务列表
	 * @return
     */
	public LinkedList<AbTaskItem> getTaskItemList() {
		return taskItemList;
	}

	/**
	 * 获取任务数量
	 * @return
	 */
	public int getTaskItemListSize() {
		return taskItemList.size();
	}
    
}

