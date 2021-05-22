package com.andbase.library.app.global;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info 用于处理退出程序时可以退出所有的activity，而编写的通用类
 */
public class AbActivityManager {

	public HashMap<String,Activity> activityMap = new HashMap<>();
	private static AbActivityManager instance;

	private AbActivityManager() {
	}

	/**
	 * 单例模式中获取唯一的AbActivityManager实例.
	 * @return
	 */
	public static AbActivityManager getInstance() {
		if (null == instance) {
			instance = new AbActivityManager();
		}
		return instance;
	}

	/**
	 * 当前数量
	 * @return
     */
	public int count(){
		return activityMap.size();
	}

	/**
	 * 添加Activity到容器中.
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityMap.put(activity.getPackageName() + "." + activity.getLocalClassName(),activity);
	}

    /**
     * 移除Activity从容器中.
     * @param activity
     */
    public void removeActivity(Activity activity) {
        removeActivity(activity.getPackageName() + "." + activity.getLocalClassName());
    }
	
	/**
	 * 移除Activity从容器中.
	 * @param className
	 */
	public void removeActivity(String className) {
		Activity activity = activityMap.remove(className);
		if(activity!=null){
			activity.finish();
		}
	}

	/**
	 * 移除Activity从容器中.
	 * @param activity
	 */
	public void onlyRemoveActivity(Activity activity) {
		activityMap.remove(activity.getPackageName() + "." + activity.getLocalClassName());
	}

	/**
	 * 遍历所有Activity并finish.
	 */
	public void clearAllActivity() {
		for (Map.Entry<String, Activity> entry : activityMap.entrySet()) {
			Activity activity = entry.getValue();
			if(activity!=null){
				activity.finish();
			}
			activityMap.remove(entry.getKey());
		}

	}
}