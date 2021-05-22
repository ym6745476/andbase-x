package com.andbase.library.asynctask;

import java.util.List;

import android.os.AsyncTask;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 下载数据的任务实现
 */
public class AbAsyncTask extends AsyncTask<AbTaskItem, Integer, AbTaskItem> {
	
	/** 监听器. */
	private AbTaskListener listener;
	
	/**
	 * Task的空构造.
	 */
	public AbAsyncTask() {
		super();
	}
	
	/**
	 * 执行任务.
	 * @param items
	 * @return
	 */
	@Override
	protected AbTaskItem doInBackground(AbTaskItem... items) {
		AbTaskItem item = items[0];
		this.listener = item.getListener();
		if (this.listener != null) {
			item.setResult(this.listener.get());
		}
		return item;
	}

	/**
	 * 取消.
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	/**
	 * 执行完成.
	 * @param item
	 */
	@Override
	protected void onPostExecute(AbTaskItem item) {
		if (this.listener != null) {
			this.listener.update(item.getResult());
		}
	}

	/**
	 * 执行前.
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	/**
	 * 进度更新.
	 * @param values
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (this.listener != null) { 
			this.listener.onProgressUpdate(values);
		}
	}

}
