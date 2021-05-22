package com.andbase.library.asynctask;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 数据执行单位
 */

public class AbTaskItem { 
	
	/** 记录的当前索引. */
	private int position;
	 
 	/** 执行完成的回调接口. */
    private AbTaskListener listener;

	/** 执行完成的结果. */
	private Object result;

	public AbTaskItem() {
		super();
	}

	public AbTaskItem(AbTaskListener listener) {
		super();
		this.listener = listener;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public AbTaskListener getListener() {
		return listener;
	}

	public AbTaskItem setListener(AbTaskListener listener) {
		this.listener = listener;
		return this;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}

