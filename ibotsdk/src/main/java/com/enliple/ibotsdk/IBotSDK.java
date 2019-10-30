package com.enliple.ibotsdk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.enliple.ibotsdk.activity.IBotSDKChatActivity;
import com.enliple.ibotsdk.common.AppPreferences;
import com.enliple.ibotsdk.common.IBotDownloadImage;
import com.enliple.ibotsdk.network.IBotNetworkAsyncTask;
import com.enliple.ibotsdk.widget.IBotChatButton;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class IBotSDK {
    private static final String TYPE_ICON = "0";
    private static final String TYPE_CLOSE = "1";
    public static boolean isGoWebView = true;
    private Context context;
    private String apiKey = "";
    private String url = "";
    private boolean isIconDownloaded = false;
    private boolean isCloseDownloaded = false;
    private String iconPath, closePath, bgColor, textColor, textStr;
    private long regDate = 0;
    public IBotSDK(Context context, String apiKey) {
        initSDK(context, apiKey);
    }
    private void initSDK(final Context context, final String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
        new IBotNetworkAsyncTask().init(apiKey, new IBotNetworkAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject jsonObject = new JSONObject(obj.toString());
                        url = jsonObject.optString("url");
                        if ( !TextUtils.isEmpty(iconPath) || !TextUtils.isEmpty(closePath) ) { // 이미지 경로가 모두 있으면
                            File iconFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + IBotDownloadImage.IMAGE_ICON + apiKey + ".png");
                            File closeFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + IBotDownloadImage.IMAGE_CLOSE + apiKey + ".png");
                            long savedDate = AppPreferences.getLong(context, AppPreferences.IBOT_REG_DATE + "_" + apiKey);
                            if ( savedDate == -1 ) { // 저장한 날짜가 없으면 다운로드
                                saveNewResources(iconFile, closeFile);
                            } else { // 저장한 날짜가 있으면
                                if ( savedDate < regDate ) { // 이미지 등록일이 최신이면
                                    saveNewResources(iconFile, closeFile);
                                } else { // 등록일이 최신이 아니면
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IBotChatButton.IBOT_IMAGE_DOWNLOAD_FINISHED));
                                }
                            }
                        } else {
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IBotChatButton.IBOT_IMAGE_DOWNLOAD_FINISHED));
                        }
                    } catch (Exception e) {
                        System.out.println(context.getResources().getString(R.string.ibot_Initialization_failed));
                        e.printStackTrace();
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IBotChatButton.IBOT_IMAGE_DOWNLOAD_FINISHED));
                    }
                } else
                    System.out.println(context.getResources().getString(R.string.ibot_Initialization_failed));
            }
        });
    }

    public void goIBotChat() {
        goIBotChat(this.context, isGoWebView);
    }

    private void goIBotChat(final Context context, final boolean isWebViewOpen) {
        if ( !TextUtils.isEmpty(url) ) {
            new IBotNetworkAsyncTask().isAlivePackage(apiKey, new IBotNetworkAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    if ( result ) {
                        Log.e("TAG", "obj :: " + obj.toString());
                        if ( apiKey != null && !TextUtils.isEmpty(apiKey) && url != null && !TextUtils.isEmpty(url) ) {
                            try {
//                                JSONObject jsonObject = (JSONObject) obj;
                                JSONObject jsonObject = new JSONObject(obj.toString());
                                boolean rt = jsonObject.optBoolean("result", false);
                                Log.e("TAG", "rt :: " + rt);
                                if ( rt ) {
                                    Log.e("TAG", "url :: " + url);
                                    if ( isWebViewOpen ) {
                                        Intent intent = new Intent( context, IBotSDKChatActivity.class );
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra( IBotSDKChatActivity.INTENT_API_URL, url );
                                        context.startActivity(intent);
                                    } else {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(browserIntent);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if ( obj != null ) {
                                    if ("true".equals(obj.toString())) {
                                        if ( isWebViewOpen ) {
                                            Intent intent = new Intent( context, IBotSDKChatActivity.class );
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra( IBotSDKChatActivity.INTENT_API_URL, url );
                                            context.startActivity(intent);
                                        } else {
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(browserIntent);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public void openIBotWithBrowser() {
        isGoWebView = false;
    }

    public void showIBotButton(final Context context, boolean isShow, int type, ViewGroup view) {
        if  (isShow) {
            IBotChatButton button = new IBotChatButton(context, apiKey, type, this);
            view.addView(button);
        }
    }
    public static String getSDKVersion() {
        return BuildConfig.IBOT_SDK_VERSION;
    }

    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getUUID(Context context) {
        UUID uuid;
        String uuidStr = "";
        try {
            final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf-8"));
            uuidStr = uuid.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return uuidStr;
    }

    public class DownloadAsyncTask extends AsyncTask<String,Void, Boolean> {
        public boolean result;
        public boolean type = false;
        public String path;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String sType = params[0];
            path = params[1];
            if (TYPE_ICON.equals(sType) ) type = true; else type = false;

            Bitmap bitmap = IBotDownloadImage.getBitmapFromURL(path);
            return IBotDownloadImage.SaveBitmapToFile(context, apiKey, bitmap, type);
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if ( type ) isIconDownloaded = true; else isCloseDownloaded = true;
            if ( isIconDownloaded && isCloseDownloaded ) {
                AppPreferences.setLong(context, AppPreferences.IBOT_REG_DATE + "_" + apiKey, regDate);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IBotChatButton.IBOT_IMAGE_DOWNLOAD_FINISHED));
            }
        }
    }

    private void saveNewResources(File iconFile, File closeFile) {
        if ( iconFile.exists() )
            iconFile.delete();
        if ( closeFile.exists() )
            closeFile.delete();
        new DownloadAsyncTask().execute(TYPE_ICON, iconPath);
        new DownloadAsyncTask().execute(TYPE_CLOSE, closePath);
        AppPreferences.setString(context, AppPreferences.IBOT_BUTTON_BG_COLOR + "_" + apiKey, bgColor);
        AppPreferences.setString(context, AppPreferences.IBOT_TEXT_COLOR + "_" + apiKey, textColor);
        AppPreferences.setString(context, AppPreferences.IBOT_TEXT + "_" + apiKey, textStr);
    }
}
