package com.andbase.library.okhttp;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 通用Http响应监听器  默认返回Object类型
 */
public abstract class AbOkHttpResponseListener<T> {
    
    /**
     * 空构造函数.
     */
    public AbOkHttpResponseListener() {
		super();
	}

	/**
	 * 获取数据开始.
	 */
    public void onStart(){};

    /**
	 * 完成后调用.
	 */
    public void onComplete(){};

    /**
     * 获取数据成功.
     */
    public void onSuccess(T result){};

    
    /**
     * 获取数据失败.
     * @param code the status code
     * @param message the content
     * @param error the error
     */
    public abstract void onError(int code, String message,Throwable error);
    
    /**
     * 显示进度.
     * @param bytesWritten the bytes written
     * @param totalSize the total size
     */
    public void onProgress(long bytesWritten, long totalSize){}

    /**
     * 获取泛型实际类型
     */
    public Class getGenericType() {
        Type genType = getClass().getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (!(params[0] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[0];
    }
}
