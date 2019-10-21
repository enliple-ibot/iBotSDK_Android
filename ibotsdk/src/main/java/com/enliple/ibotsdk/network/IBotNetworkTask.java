package com.enliple.ibotsdk.network;

import android.content.Context;

import org.json.JSONObject;

import java.util.HashMap;

public class IBotNetworkTask extends IBotCommonApi {
    private static final int MODE_GET = -1;
    private static final int MODE_POST = -2;
    private static final int MODE_PUT = -3;
    private static final int MODE_DELETE = -4;

    private OnDefaultObjectCallbackListener defaultObjectCallbackListener = null;
    private Context context;
    private int modeIndex;
    private String url;

    public IBotNetworkTask(Context context) {
        this.context = context;
    }

    public void init(String mallId, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        url = IBotURL.INIT;
        this.defaultObjectCallbackListener = defaultObjectCallbackListener;

        param = new HashMap<>();
        param.put("mallId", mallId);

        modeIndex = MODE_GET;
        execute();
    }

    public void isAlivePackage(String mallId, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        url = IBotURL.IS_ALIVE_PACKAGE;
        this.defaultObjectCallbackListener = defaultObjectCallbackListener;

        param = new HashMap<>();
        param.put("mallId", mallId);

        modeIndex = MODE_GET;
        execute();
    }

    public void getChatUrl(String mallId, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        url = IBotURL.HOST;
        this.defaultObjectCallbackListener = defaultObjectCallbackListener;

        modeIndex = MODE_GET;
        execute();
    }

    public interface OnDefaultObjectCallbackListener {
        public void onResponse(boolean result, Object obj);
    }

    public void execute() {
        if (modeIndex == MODE_GET) {
            new IBotAsyncApi(url, param, body, new IBotCommonApi.CallbackObjectResponse() {
                @Override
                public void onResponse(Object result) {
                    if (defaultObjectCallbackListener != null)
                        defaultObjectCallbackListener.onResponse(true, result);
                }

                @Override
                public void onError(String error) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put(IBotKey.NETWORK_ERROR, error);
                        if (defaultObjectCallbackListener != null)
                            defaultObjectCallbackListener.onResponse(false, object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(this.context);
        } else if (modeIndex == MODE_POST) {
            new IBotAsyncApi(url, param, body, new IBotCommonApi.CallbackObjectResponse() {
                @Override
                public void onResponse(Object result) {
                    if (defaultObjectCallbackListener != null)
                        defaultObjectCallbackListener.onResponse(true, result);
                }

                @Override
                public void onError(String error) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put(IBotKey.NETWORK_ERROR, error);
                        if (defaultObjectCallbackListener != null)
                            defaultObjectCallbackListener.onResponse(false, object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).postExecute(this.context);
        } else if (modeIndex == MODE_PUT) {
            new IBotAsyncApi(url, param, body, new IBotCommonApi.CallbackObjectResponse() {
                @Override
                public void onResponse(Object result) {

                    if (defaultObjectCallbackListener != null)
                        defaultObjectCallbackListener.onResponse(true, result);
                }

                @Override
                public void onError(String error) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put(IBotKey.NETWORK_ERROR, error);
                        if (defaultObjectCallbackListener != null)
                            defaultObjectCallbackListener.onResponse(false, object);
                    } catch (Exception e) {
                    }
                }
            }).putExecute(this.context);
        } else if ( modeIndex == MODE_DELETE) {
            new IBotAsyncApi(url, param, body,  new IBotCommonApi.CallbackObjectResponse() {
                @Override
                public void onResponse(Object result) {

                    if (defaultObjectCallbackListener != null)
                        defaultObjectCallbackListener.onResponse(true, result);
                }

                @Override
                public void onError(String error) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put(IBotKey.NETWORK_ERROR, error);
                        if (defaultObjectCallbackListener != null)
                            defaultObjectCallbackListener.onResponse(false, object);
                    } catch (Exception e) {
                    }
                }
            }).deleteExecute(this.context);
        }
    }
}
