package com.andbase.library.okhttp;


import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import okhttp3.OkHttpClient;


@GlideModule
public class AbOkHttpGlideModule extends AppGlideModule {

    /**
     OkHttp 是一个底层网络库(相较于 Cronet 或 Volley 而言)，尽管它也包含了 SPDY 的支持。
     OkHttp 与 Glide 一起使用可以提供可靠的性能，并且在加载图片时通常比 Volley 产生的垃圾要少。
     对于那些想要使用比 Android 提供的 HttpUrlConnection 更 nice 的 API，
      或者想确保网络层代码不依赖于 app 安装的设备上 Android OS 版本的应用，OkHttp 是一个合理的选择。
      如果你已经在 app 中某个地方使用了 OkHttp ，这也是选择继续为 Glide 使用 OkHttp 的一个很好的理由，就像选择其他网络库一样。
     添加 OkHttp 集成库的 Gradle 依赖将使 Glide 自动开始使用 OkHttp 来加载所有来自 http 和 https URL 的图片
     * @param context
     * @param glide
     * @param registry
     */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        OkHttpClient okHttpClient = AbOkHttpClient.getNoSSLTrustOkHttpClient().build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));
    }

}

