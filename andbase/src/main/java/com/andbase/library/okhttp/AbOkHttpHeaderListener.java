package com.andbase.library.okhttp;

import java.util.HashMap;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info HTTP请求头创建
 */
public interface AbOkHttpHeaderListener {

    HashMap<String,String> onCreateHeader(String url, String method,String jsonBody);

}
