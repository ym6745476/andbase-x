package com.andbase.library.asynctask;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 数据监听器
 */
public abstract class AbTaskListener<T> {

	/**
	 * 执行开始
	 * @return 返回的结果对象
	 */
	public abstract T get();

	/**
	 * 执行开始后调用.
	 * @param obj
	 */
	public abstract void update(T obj);

	/**
	 * 监听进度变化.
	 * 
	 * @param values the values
	 */
	public void onProgressUpdate(Integer... values) {
	};

}
