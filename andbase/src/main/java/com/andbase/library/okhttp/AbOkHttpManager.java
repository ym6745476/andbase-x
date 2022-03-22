package com.andbase.library.okhttp;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import android.text.TextUtils;
import android.webkit.WebSettings;

import com.andbase.library.utils.AbAppUtil;
import com.andbase.library.utils.AbFileUtil;
import com.andbase.library.utils.AbJsonUtil;
import com.andbase.library.utils.AbLogUtil;
import com.andbase.library.utils.AbStrUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AbOkHttpManager {

    public static Context context;
    public static AbOkHttpManager manager;
    private static  final String TAG = "HTTP Manager";

    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_DELETE = "DELETE";


    /** OKHttp */
    public OkHttpClient httpClient = null;

    /**请求列表*/
    public List<Call> callList = new ArrayList<>();

    /** 请求头. */
    public HashMap<String,String> headerMap = null;
    /** 当前URL. */
    public String url,method;
    /** 当前参数. */
    public AbOkRequestParams params;

    /** 请求自定义. */
    public AbOkHttpHeaderListener httpHeaderCreateListener = null;

    /** 缓存. */
    public long maxCacheSize = 100 * 1024 * 1024;
    public Cache cache;
    public int cacheType = 0;
    public static final int CACHAE_TYPE_NOCACHE = 0;
    public static final int CACHAE_TYPE_DEFAULT = 1;
    public static final int CACHAE_TYPE_OPTIMIZE = 2;
    public String downLoadPath;

    /** 全部缓存60s */
    public Interceptor interceptorDefault = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            AbLogUtil.i(TAG, "Interceptor Cache Response = " + response.cacheResponse());
            String cacheControl = request.cacheControl().toString();
            if (TextUtils.isEmpty(cacheControl)) {
                cacheControl = "public, max-age=60";
            }
            return response.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();
        }
    };

    /** 离线时，可以获取缓存，在线时获取最新数据 */

    public Interceptor interceptorOptimize = new Interceptor() {
        @Override
        @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!AbAppUtil.isNetworkAvailable(context)) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                AbLogUtil.i(TAG, "没有网络，强制读取缓存");
            }
            Response response = chain.proceed(request);
            AbLogUtil.i(TAG, "Interceptor Cache Response = " + response.cacheResponse());

            if (AbAppUtil.isNetworkAvailable(context)) {
                // 有网络时 设置缓存超时时间为120s;
                int maxAge = 120;
                AbLogUtil.i(TAG, "有网络时，设置缓存时间为120分钟");
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build();
            } else {
                int maxStale = 60 * 60 * 24;
                AbLogUtil.i(TAG, "无网络时，设置超时为1天");
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
            }
            return response;
        }
    };

    public static AbOkHttpManager getInstance(Context context){
        manager = getInstance(context,CACHAE_TYPE_NOCACHE);
        return manager;
    }

    public static AbOkHttpManager getInstance(Context context,int cacheType){
        if(manager == null){
            manager = new AbOkHttpManager(context,cacheType);
        }
        return manager;
    }

    public AbOkHttpManager(Context context,int cacheType) {
        this.context = context;
        this.cacheType = cacheType;
        this.headerMap = new HashMap<String,String>();
        String cacheDirPath = AbFileUtil.getCacheDownloadDir(context);
        if(!AbStrUtil.isEmpty(cacheDirPath)){
            File cacheDir = new File(cacheDirPath);
            this.cache = new Cache(cacheDir, maxCacheSize);
            if(cacheType == CACHAE_TYPE_DEFAULT){
                this.httpClient = AbOkHttpClient.getNoSSLTrustOkHttpClient().cache(cache).addInterceptor(interceptorDefault).addNetworkInterceptor(interceptorDefault).build();
            }else if(cacheType == CACHAE_TYPE_OPTIMIZE){
                this.httpClient = AbOkHttpClient.getNoSSLTrustOkHttpClient().cache(cache).addInterceptor(interceptorOptimize).addNetworkInterceptor(interceptorOptimize).build();
            }else{
                this.httpClient = AbOkHttpClient.getNoSSLTrustOkHttpClient().build();
            }
        }else{
            this.httpClient = AbOkHttpClient.getNoSSLTrustOkHttpClient().build();
        }
    }

    public <T>void get(String url, AbOkHttpResponseListener<T> responseListener) {
        request(url,HTTP_GET,null,responseListener);
    }

    public  <T>void get(String url, AbOkRequestParams params, AbOkHttpResponseListener<T> responseListener) {
        request(url,HTTP_GET,params,responseListener);
    }

    public  <T>void post(String url, AbOkHttpResponseListener<T> responseListener) {
        request(url,HTTP_POST,null,responseListener);
    }

    public  <T>void post(String url, AbOkRequestParams params, AbOkHttpResponseListener<T> responseListener) {
        request(url,HTTP_POST,params,responseListener);
    }

    public  <T>void postSync(String url, AbOkRequestParams params, AbOkHttpResponseListener<T> responseListener) {
        request(url,HTTP_POST,params,responseListener);
    }

    public  <T>void put(String url, AbOkHttpResponseListener<T> responseListener) {
        request(url,HTTP_PUT,null,responseListener);
    }

    public  <T>void put(String url, AbOkRequestParams params, AbOkHttpResponseListener<T> responseListener) {
        request(url,HTTP_PUT,params,responseListener);
    }

    public  <T>void delete(String url, AbOkHttpResponseListener<T> responseListener) {
        request(url,HTTP_DELETE,null,responseListener);
    }

    public <T> void delete(String url, AbOkRequestParams params, AbOkHttpResponseListener<T> responseListener) {
        request(url,HTTP_DELETE,params,responseListener);
    }

    public  <T>void download(String url,String downloadPath, AbOkHttpResponseListener<T> responseListener) {
        this.downLoadPath = downloadPath;
        request(url,HTTP_GET,null,responseListener);
    }

    public <T> void  request(final String url, final String method, final AbOkRequestParams params, final AbOkHttpResponseListener<T> responseListener){
        //请求开始
        responseListener.onStart();
        AbLogUtil.i(TAG, "[request]:" + url + "\n");
        Observable.create(new ObservableOnSubscribe<Response>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> emitter){
                try{
                    AbLogUtil.i(TAG, "[subscribe] 线程:" + Thread.currentThread().getName() + "\n");

                    if(cacheType == CACHAE_TYPE_NOCACHE){
                        if(!AbAppUtil.isNetworkAvailable(context)){
                            AbLogUtil.i(TAG, "网络是无效的：" + url);
                            throw new ConnectException("Connect Failed");
                        }
                    }
                    if (!emitter.isDisposed()){
                        {
                            Request.Builder builder = new Request.Builder();

                            final String requestUrl = setUrlParams(url, method, params);

                            if (params != null && params.getFileParams().size() > 0) {
                                builder.post(getMultipartBody(params, responseListener));
                            } else {
                                //设置参数
                                switch (method) {
                                    case HTTP_GET:
                                        break;
                                    case HTTP_POST:
                                        builder.post(getRequestBody(params));
                                        break;
                                    case HTTP_PUT:
                                        builder.put(getRequestBody(params));
                                        break;
                                    case HTTP_DELETE:
                                        builder.delete(getRequestBody(params));
                                        break;
                                }
                            }

                            builder.url(requestUrl);
                            //设置User-Agent
                            builder.removeHeader("User-Agent").addHeader("User-Agent", getUserAgent());
                            //请求头
                            if (httpHeaderCreateListener != null) {
                                HashMap<String, String> headerCustom = httpHeaderCreateListener.onCreateHeader(requestUrl, method, (!method.endsWith("GET") && params != null) ? params.getParamsJson() : null);
                                if (headerCustom != null) {
                                    headerMap.putAll(headerCustom);
                                }
                            }

                            Iterator iterator = headerMap.entrySet().iterator();
                            while (iterator.hasNext()) {
                                Map.Entry entry = (Map.Entry) iterator.next();
                                String key = (String) entry.getKey();
                                String val = (String) entry.getValue();
                                builder.addHeader(key, val);
                            }

                            final Request request = builder.build();
                            Call call = httpClient.newCall(request);


                            StringBuffer requestBuffer = new StringBuffer();
                            requestBuffer.append("[" + method + "]" + requestUrl + ",");

                            requestBuffer.append("[Body]");
                            if (params != null && params.bodyParams.size() > 0) {
                                requestBuffer.append(params.getParamsBody());
                            }
                            if (params != null && params.jsonParams.size() > 0) {
                                requestBuffer.append(params.getParamsJson());
                            }
                            if (params != null && params.fileParams.size() > 0) {
                                requestBuffer.append(params.getParamsFile());
                            }

                            requestBuffer.append("[Headers]");
                            for (int i = 0; i < request.headers().size(); i++) {
                                requestBuffer.append(request.headers().name(i) + ":" + request.headers().value(i));
                            }

                            AbLogUtil.i(TAG, requestBuffer.toString());

                            //返回结果
                            Response response = call.execute();

                            AbLogUtil.i(TAG, "[Response Network]" + response.networkResponse());
                            if(response.cacheResponse()!=null){
                                AbLogUtil.i(TAG, "[Response Cache]" + response.cacheResponse());
                            }

                            if(response.isSuccessful()){
                                emitter.onNext(response);
                            }else{
                                throw new ConnectException("Connect Failed");
                            }

                            response.body().close();

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        }).map(new Function<Response, T>() {

            @Override
            public T apply(@NonNull Response response) {

                //AbLogUtil.i(TAG, "[Map Apply] 线程:" + Thread.currentThread().getName() + "\n");
                try{
                    if(responseListener.getGenericType().equals(String.class)){
                        String result = response.body().string();
                        AbLogUtil.i(TAG, "[Response]" + url + "," +  result);
                        return (T)result;
                    }else if(responseListener.getGenericType().equals(Byte[].class)){
                        //字节
                        byte[] result = response.body().bytes();
                        return (T)result;
                    }else if(responseListener.getGenericType().equals(File.class)){
                        //文件
                        if(AbStrUtil.isEmpty(downLoadPath)){
                            downLoadPath = AbFileUtil.getFileDownloadDir(context);
                        }
                        if(!new File(downLoadPath).exists()){
                            new File(downLoadPath).getParentFile().mkdirs();
                        }
                        File file = new File(downLoadPath + "/" + getFileName(url));
                        writeToFile(context,response.body(),file,responseListener);
                        return (T)file;
                    }else if(responseListener.getGenericType().getSuperclass().equals(AbOkJsonModel.class)){
                        String result = response.body().string();
                        AbLogUtil.i(TAG, "[Response]" + url + "," +  result);
                        return (T)AbJsonUtil.fromJson(result, responseListener.getGenericType());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        }).subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<T>() {

            @Override
            public void onSubscribe(Disposable d) {
                //AbLogUtil.i(TAG, "[onSubscribe] ========================");
            }

            @Override
            public void onNext(T result) {
                //AbLogUtil.i(TAG, "[onNext] 线程:" + Thread.currentThread().getName() + "\n");
                try {
                    responseListener.onSuccess(result);
                    responseListener.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                AbLogUtil.i(TAG, "[onError]" + e.getMessage());
                responseListener.onError(500,"服务连接失败!",new ConnectException());
                responseListener.onComplete();

            }

            @Override
            public void onComplete() {
                //AbLogUtil.i(TAG, "[onComplete] ===========不会执行================");
                //responseListener.onComplete();
            }
        });

    }

    private String setUrlParams(String url,final String method, final AbOkRequestParams params){

        if(params!=null && params.size()[0] > 0){

            for (ConcurrentHashMap.Entry<String, String> entry : params.getUrlParams().entrySet()) {
                String key = "{" + entry.getKey() +"}";
                if(url.contains(key)){
                    url = url.replace(key,entry.getValue());
                    params.getUrlParams().remove(entry.getKey());
                }
            }

            if(method.equals(HTTP_GET)){
                if(params.size()[0] > 0){

                    if(params.getUrlParams().size() > 0  && url.indexOf("?")==-1){
                        url += "?";
                    }

                    for (String key: params.urlParams.keySet()){
                        url = url + key+"="+params.urlParams.get(key)+"&";
                    }
                    url = url.substring(0,url.length()-1);
                }
            }
        }
        return url;
    }

    /**
     * 得到body对象
     */
    private RequestBody getRequestBody(AbOkRequestParams params) {
        /**
         * 首先判断json参数是否为空
         */
        if(params!= null && params.getJsonParams().size() > 0){
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            return RequestBody.create(JSON, params.getParamsJson());
        }

        /**
         * post,put,delete都需要body，但也都有body等于空的情况，此时也应该有body对象，但body中的内容为空
         */
        FormBody.Builder formBody = new FormBody.Builder();
        if(params!= null && params.bodyParams != null) {
            for (String key : params.bodyParams.keySet()) {
                formBody.add(key, params.bodyParams.get(key));
            }
        }
        return formBody.build();
    }


    /**
     * 文件Map，可能带有键值对参数
     */
    private MultipartBody getMultipartBody(AbOkRequestParams params,final AbOkHttpResponseListener responseListener) {
        if(params.getFileParams().size() > 0){

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            if(params.bodyParams != null) {
                for (String key : params.bodyParams.keySet()) {
                    builder.addFormDataPart(key, params.bodyParams.get(key));
                }
            }

            for (String key : params.getFileParams().keySet()){
                AbOKProgressBody progressRequestBody = new AbOKProgressBody(params.getFileParams().get(key), "application/octet-stream", new AbOKProgressBody.ProgressListener() {
                    @Override
                    public void transferred(long size,long total) {
                        responseListener.onProgress(size,total);
                    }
                });
                //builder.addFormDataPart(key,params.getFileParams().get(key).getName(), RequestBody.create(MediaType.parse("multipart/form-data"), params.getFileParams().get(key)));
                builder.addFormDataPart(key,params.getFileParams().get(key).getName(),progressRequestBody);
            }

            return builder.build();

        }else{
            return null;
        }
    }


    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**
     * 将流的数据写入文件并回调进度.
     * @param context the context
     * @param responseBody the responseBody
     * @param file the file
     * @param responseListener the response listener
     */
    private <T>void writeToFile(Context context, ResponseBody responseBody, File file, AbOkHttpResponseListener<T> responseListener){


        if(responseBody == null){
            return;
        }

        InputStream inStream = null;
        FileOutputStream outStream = null;
        try {

            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            file.deleteOnExit();
            file.createNewFile();

            inStream = responseBody.byteStream();
            long contentLength = responseBody.contentLength();
            outStream = new FileOutputStream(file);
            if (inStream != null) {

                byte[] tmp = new byte[1024];
                int l, count = 0;
                while ((l = inStream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                    count += l;
                    outStream.write(tmp, 0, l);
                    //进度通知
                    if(responseListener!=null){
                        responseListener.onProgress(count, contentLength);
                    }

                }
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                if(inStream!=null){
                    inStream.close();
                }
                if(outStream!=null){
                    outStream.flush();
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void cancelAll(){
        try{
            for(int i =0;i<callList.size();i++){
                Call call = callList.get(i);
                if(call!=null){
                    call.cancel();
                }
                callList.remove(i);
                i--;
            }
            AbLogUtil.e(context,"取消任务完成");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public AbOkHttpHeaderListener getHttpHeaderCreateListener() {
        return httpHeaderCreateListener;
    }

    public void setHttpHeaderCreateListener(AbOkHttpHeaderListener httpHeaderCreateListener) {
        this.httpHeaderCreateListener = httpHeaderCreateListener;
    }

    public String getUserAgent() {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }

        sb.append(" ");
        sb.append(android.os.Build.BRAND);
        sb.append("/");
        sb.append(android.os.Build.MODEL);
        sb.append("/");
        sb.append("Android");
        sb.append("/");
        sb.append(android.os.Build.VERSION.RELEASE);
        sb.append("/");
        sb.append(AbAppUtil.getPackageInfo(context).packageName);
        sb.append("/");
        sb.append(AbAppUtil.getPackageInfo(context).versionName);

        return sb.toString();
    }

}
