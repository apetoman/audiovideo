package com.eju.cy.audiovideo.net;

import android.content.Context;
import android.util.Log;


import com.eju.cy.audiovideo.BuildConfig;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    public static final String SERVER_URL = "https://yun.jiandanhome.com/";
    private static RetrofitManager instance;

    public static RetrofitManager getDefault() {


        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (instance == null) {
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }


    public AppNetInterface provideClientApi(final Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .client(genericClient(context))
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .build();
        return retrofit.create(AppNetInterface.class);

    }


    private OkHttpClient genericClient(final Context context) {


        OkHttpClient httpClient = new OkHttpClient.Builder().
                connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(60, TimeUnit.SECONDS).
                writeTimeout(60, TimeUnit.SECONDS).
                addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        String userId = "1";
                        String token = "7d9f83cba7896e8c061565ffcb44a3d3d129e084";

                        Log.w("UploadCard---", "userId" + userId + "\n" + token);
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("User-Id", userId)
                                .addHeader("User-Token", token)
                                .build();
                        return chain.proceed(request);
                    }

                })

                .build();

        return httpClient;
    }



    /*-------------------------*/

    private OkHttpClient genericClient(final Context context, final String userNo, final String userToken,final String type) {


        OkHttpClient httpClient = new OkHttpClient.Builder().
                        connectTimeout(60, TimeUnit.SECONDS).
                        readTimeout(60, TimeUnit.SECONDS).
                        writeTimeout(60, TimeUnit.SECONDS).
                        addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {

                                String userId = userNo;
                                String token = userToken;

//                        Log.w("UploadCard---","userId" + userId + "\n" + token);
                                Request request = chain.request()
                                        .newBuilder()
                                        .addHeader("User-Id", userId)
                                        .addHeader("User-Token", token)
                                        .addHeader("X-REQUESTED-WITH", "json")
                                        .addHeader("Http-Plat", type)
                                        .build();
                                return chain.proceed(request);
                            }

                        })
                .addInterceptor(  new LoggingInterceptor.Builder()
                        .loggable(BuildConfig.DEBUG)
                        .setLevel(Level.BASIC)
                        .log(Platform.INFO)
                        .request("Request")
                        .response("Response")
                        .build())

                .build();

        return httpClient;
    }


    public AppNetInterface provideClientApi(final Context context, final String userId, final String userToken,final String type) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .client(genericClient(context, userId, userToken,type))
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .build();
        return retrofit.create(AppNetInterface.class);

    }


}

