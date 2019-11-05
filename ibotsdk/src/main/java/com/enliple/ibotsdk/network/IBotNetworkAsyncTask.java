package com.enliple.ibotsdk.network;

import com.enliple.ibotsdk.common.IBotKey;

import java.util.HashMap;


public class IBotNetworkAsyncTask extends IBotNetworkApi {

    private static final int MODE_GET = -1;
    private static final int MODE_POST = -2;

    private OnDefaultObjectCallbackListener defaultObjectCallbackListener;
    private int modeIndex;

    public void init(String apiKey, String uuid, String sdkVersion, String osVersion, String packageName, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        url = IBotURL.INIT;
        this.defaultObjectCallbackListener = defaultObjectCallbackListener;

        param = new HashMap<>();
        param.put(IBotKey.API_KEY, apiKey);
        param.put(IBotKey.UUID, uuid);
        param.put(IBotKey.SDK_VERSION, sdkVersion);
        param.put(IBotKey.OS_VERSION, osVersion);
        param.put(IBotKey.PACKAGE_NAME, packageName);

        modeIndex = MODE_GET;
        execute();
    }

    public void isAlivePackage(String apiKey, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        url = IBotURL.IS_ALIVE_PACKAGE;
        this.defaultObjectCallbackListener = defaultObjectCallbackListener;

        param = new HashMap<>();
        param.put(IBotKey.API_KEY, apiKey);

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
