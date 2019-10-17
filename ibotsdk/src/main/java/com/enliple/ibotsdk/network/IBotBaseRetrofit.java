package com.enliple.ibotsdk.network;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class IBotBaseRetrofit {

    protected static Object retrofit(Context context, Class<?> className) {
        String host = IBotURL.HOST;
        Log.e("TAG", "host :: " + host);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.connectTimeout(20, TimeUnit.SECONDS);
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .build();
                return chain.proceed(request);
            }
        });

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addNetworkInterceptor(logging);

        OkHttpClient okHttpClient = builder.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(host).client(okHttpClient).build();
        return retrofit.create(className);
    }
}
