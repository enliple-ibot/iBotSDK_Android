package com.enliple.ibotsdk.network;

import android.content.Context;

import java.util.HashMap;


public class IBotNetworkAsyncTask extends IBotNetworkApi {

    private static final int MODE_GET = -1;
    private static final int MODE_POST = -2;

    private OnDefaultObjectCallbackListener defaultObjectCallbackListener;
    private int modeIndex;

    public void init(String apiKey, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        url = IBotURL.INIT;
        this.defaultObjectCallbackListener = defaultObjectCallbackListener;

        param = new HashMap<>();
        param.put("apiKey", apiKey);

        modeIndex = MODE_GET;
        execute();
    }

    public void isAlivePackage(String apiKey, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        url = IBotURL.IS_ALIVE_PACKAGE;
        this.defaultObjectCallbackListener = defaultObjectCallbackListener;

        param = new HashMap<>();
        param.put("apiKey", apiKey);

        modeIndex = MODE_GET;
        execute();
    }

    public void sendInfos(String strJson, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        url = IBotURL.SEND_INFOS;
        this.defaultObjectCallbackListener = defaultObjectCallbackListener;

        this.strJson = strJson;
        modeIndex = MODE_POST;
        execute();
    }

    public void execute() {
        if ( modeIndex == MODE_GET ) {
            getExecute(new CallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    if ( defaultObjectCallbackListener != null ) {
                        defaultObjectCallbackListener.onResponse(result, obj);
                    }
                }
            });
        } else if ( modeIndex == MODE_POST ) {
            postExecute(new CallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    if ( defaultObjectCallbackListener != null ) {
                        defaultObjectCallbackListener.onResponse(result, obj);
                    }
                }
            });
        }
    }

    public interface OnDefaultObjectCallbackListener {
        public void onResponse(boolean result, Object obj);
    }
}
