package com.enliple.ibotsdk.common;

public class Key {
    public static final String API_KEY = "apiKey";
    public static final String UUID = "uuid";
    public static final String SDK_VERSION = "sdkVersion";
    public static final String OS_VERSION = "osVersion";
    public static final String APP_KIND = "appKind";
    public static final String UID = "uid";

                                object.put("uid", uid);
    JSONObject dataObject = new JSONObject();
                                    dataObject.put("uuid", IBotSDK.getUUID(getApplicationContext()));
                                    dataObject.put("os_version", IBotSDK.getOSVersion());
                                    dataObject.put("os_type", "Android");
                                    dataObject.put("sdk_version", IBotSDK.getSDKVersion());
                                    dataObject.put("device", IBotSDK.getModel());
                                    object.put("data", dataObject);
}
