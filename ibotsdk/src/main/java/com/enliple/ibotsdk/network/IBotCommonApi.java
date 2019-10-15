package com.enliple.ibotsdk.network;

import android.content.Context;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;

public class IBotCommonApi {
    public String url;
    public Callback<ResponseBody> listenerRetrofit;
    public Map<String, String> param;
    public Map<String, String> header;
    public String headerStr;
    public RequestBody body;

    public void execute(Context context) {
        if (context == null)
            return;
        if (param != null && param.size() > 0)
            IBotRetrofitService.api(context).IBotGetAPI(url, param).enqueue(listenerRetrofit);
        else
            IBotRetrofitService.api(context).IBotGetAPI(url).enqueue(listenerRetrofit);
    }

    public void postExecute(Context context) {
        if (context == null)
            return;

        if (param != null && param.size() > 0) {
            IBotRetrofitService.api(context).IBotPostAPI(url, param).enqueue(listenerRetrofit);
        } else if(body != null) {
            IBotRetrofitService.api(context).IBotPostAPI1(url, body).enqueue(listenerRetrofit);
        } else {
            IBotRetrofitService.api(context).IBotPostAPI(url).enqueue(listenerRetrofit);
        }
    }

    public void putExecute(Context context) {
        if (param != null && param.size() > 0) {
            IBotRetrofitService.api(context).IBotPutAPI(url, param).enqueue(listenerRetrofit);
        } else if(body != null) {
            IBotRetrofitService.api(context).IBotPutAPI1(url, body).enqueue(listenerRetrofit);
        } else {
            IBotRetrofitService.api(context).IBotPutAPI(url).enqueue(listenerRetrofit);
        }
    }

    public void deleteExecute(Context context) {
        if (param != null && param.size() > 0) {
            IBotRetrofitService.api(context).IBotDeleteAPI(url, param).enqueue(listenerRetrofit);
        } else if(body != null) {
            IBotRetrofitService.api(context).IBotDeleteAPI1(url, body).enqueue(listenerRetrofit);
        } else {
            IBotRetrofitService.api(context).IBotDeleteAPI(url).enqueue(listenerRetrofit);
        }
    }

    public interface CallbackStringResponse {
        void onResponse(String result);

        void onError(String error);
    }

    public interface CallbackObjectResponse {
        void onResponse(Object result);

        void onError(String error);
    }
}
