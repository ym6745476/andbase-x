package com.andbase.library.okhttp;


import com.andbase.library.utils.AbJsonUtil;


import java.io.File;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info Http请求参数
 */

public class AbOkRequestParams {

	/** url参数. */
	protected ConcurrentHashMap<String, String> urlParams;

	/** body参数. */
	protected ConcurrentHashMap<String, String> bodyParams;

	/** 文件参数. */
	protected ConcurrentHashMap<String, File> fileParams;

	/** json参数. */
	protected ConcurrentHashMap<String, Object> jsonParams;


    /**
     * 默认构造函数
     */
    public AbOkRequestParams() {
        super();
        urlParams = new ConcurrentHashMap<String, String>();
		bodyParams = new ConcurrentHashMap<String, String>();
        fileParams = new ConcurrentHashMap<String, File>();
		jsonParams = new ConcurrentHashMap<String, Object>();
    }

	/**
	 * 添加一个文件参数
	 * @param attr 属性名
	 * @param file 文件
	 */
	public void putFile(String attr, File file) {
		if (attr != null && file != null) {
			fileParams.put(attr, file);
		}
	}

	/**
	 * 添加一个int参数
	 * @param attr
	 * @param value
	 */
	public void putUrl(String attr, int value) {
		try {
			urlParams.put(attr, String.valueOf(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一个String参数
	 * @param attr
	 * @param value
	 */
	public void putUrl(String attr, String value) {
		try {
			if (attr != null && value != null) {
				urlParams.put(attr, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一个int参数
	 * @param attr
	 * @param value
	 */
	public void putBody(String attr, int value) {
		try {
			bodyParams.put(attr, String.valueOf(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一个String参数
	 * @param attr
	 * @param value
	 */
	public void putBody(String attr, String value) {
		try {
			if (attr != null && value != null) {
				bodyParams.put(attr, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一个json参数
	 * @param attr
	 * @param value
	 */
	public void putJson(String attr, int value) {
		try {
			jsonParams.put(attr, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一个json参数
	 * @param attr
	 * @param value
	 */
	public void putJson(String attr, String value) {
		try {
			jsonParams.put(attr, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一个json参数
	 * @param attr
	 * @param value
	 */
	public void putJson(String attr, Object value) {
		try {
			jsonParams.put(attr, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一个String参数
	 * @param json
	 */
	public void putJson(String json) {
		try {
			jsonParams.put("json2018", json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除参数
	 * @param attr
	 */
	public void remove(String attr) {
		try {
			if (attr != null) {
				urlParams.remove(attr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 参数数量
	 */
	public int[] size() {
		return new int[]{urlParams.size(),fileParams.size(),jsonParams.size()};
	}

	public String getParamsJson(){
		if(jsonParams.size() == 1 && jsonParams.get("json2018")!= null){
			return (String)jsonParams.get("json2018");
		}else{
			return AbJsonUtil.toJson(jsonParams);
		}
	}

	public String getParamsBody(){
		StringBuffer params = new StringBuffer();
		if(bodyParams.size() > 0){
			for (ConcurrentHashMap.Entry<String, String> entry : bodyParams.entrySet()) {
				params.append(entry.getKey()+":"+entry.getValue() + ",");
			}
		}
		return params.toString();
	}

	public String getParamsFile(){
		StringBuffer params = new StringBuffer();
		if(fileParams.size() > 0){
			for (ConcurrentHashMap.Entry<String, File> entry : fileParams.entrySet()) {
				params.append(entry.getKey()+":"+entry.getValue().getPath() + ",");
			}
		}
		return params.toString();
	}

	public ConcurrentHashMap<String, String> getUrlParams() {
		return urlParams;
	}

	public ConcurrentHashMap<String, File> getFileParams() {
		return fileParams;
	}

	public ConcurrentHashMap<String, Object> getJsonParams() {
		return jsonParams;
	}

	public ConcurrentHashMap<String, String> getBodyParams() {
		return bodyParams;
	}

}
