package com.enliple.ibotsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.enliple.ibotsdk.activity.IBotSDKChatActivity;
import com.enliple.ibotsdk.common.IBotAppPreferences;
import com.enliple.ibotsdk.common.IBotDownloadImage;
import com.enliple.ibotsdk.network.IBotNetworkAsyncTask;
import com.enliple.ibotsdk.widget.IBotChatButton;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class IBotSDK {
    private static final int SET_RESOURCE = 1;
    private static final String TYPE_ICON = "0";
    private static final String TYPE_CLOSE = "1";

    private GestureDetector gestureDetector;

    private Context context;
    private String apiKey = "";
    private String url = "";
    private boolean isGoWebView = true;
    private boolean isIconDownloaded = false;
    private boolean isCloseDownloaded = false;
    private String iconPath, closePath, bgColor, textColor, textStr;
    private long regDate = 0;
    private int orientation = -100;

    private float moveX = 0f;
    private float moveY = 0f;


    private IBotChatButton button;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if ( msg.what == SET_RESOURCE ) {
                if ( button != null )
                    button.onReceived();
            }
        }
    };
    public IBotSDK(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
    }

    private void initSDK(final Context context, final String apiKey) {

        new IBotNetworkAsyncTask().init(apiKey, getUUID(context), getSDKVersion(), getOSVersion(), context.getPackageName(), new IBotNetworkAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject jsonObject = new JSONObject(obj.toString());
                        url = jsonObject.optString("url");
                        Log.e("TAG", "url :: " + url);
//                        iconPath = "https://cdn.onlinewebfonts.com/svg/img_199295.png";
//                        closePath = "https://bot.istore.camp/chatImages/common/ico_close_floating.png";
//                        iconPath = "https://bot.istore.camp/chatImages/common/showbot_icon.png";
//                        closePath = "https://bot.istore.camp/chatImages/common/ico_close_floating.png";
                        if ( !TextUtils.isEmpty(iconPath) || !TextUtils.isEmpty(closePath) ) {
                            File iconFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + IBotDownloadImage.IMAGE_ICON + apiKey + IBotDownloadImage.IMAGE_FILE_EXTENSION);
                            File closeFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + IBotDownloadImage.IMAGE_CLOSE + apiKey + IBotDownloadImage.IMAGE_FILE_EXTENSION);
                            long savedDate = IBotAppPreferences.getLong(context, IBotAppPreferences.IBOT_REG_DATE + "_" + apiKey);
                            if ( savedDate == -1 ) { // 저장한 날짜가 없으면 다운로드
                                saveNewResources(iconFile, closeFile);
                            } else { // 저장한 날짜가 있으면
                                if ( savedDate < regDate ) // 이미지 등록일이 최신이면
                                    saveNewResources(iconFile, closeFile);
                                else // 등록일이 최신이 아니면
                                    handler.sendEmptyMessage(SET_RESOURCE);
                            }
                        } else
                            handler.sendEmptyMessage(SET_RESOURCE);
                    } catch (Exception e) {
                        System.out.println(context.getResources().getString(R.string.ibot_Initialization_failed));
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void goIBotChat() {
        goIBotChat(this.context);
    }

    private void goIBotChat(final Context context) {
        if ( !TextUtils.isEmpty(url) ) {
            new IBotNetworkAsyncTask().isAlivePackage(apiKey, new IBotNetworkAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    if ( result ) {
                        if ( apiKey != null && !TextUtils.isEmpty(apiKey) && url != null && !TextUtils.isEmpty(url) ) {
                            try {
                                JSONObject jsonObject = new JSONObject(obj.toString());
                                boolean rt = jsonObject.optBoolean("result", false);
                                if ( rt ) {
                                    if ( isGoWebView ) {
                                        Intent intent = new Intent( context, IBotSDKChatActivity.class );
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra( IBotSDKChatActivity.INTENT_API_URL, url );
                                        if ( orientation != -100 )
                                            intent.putExtra(IBotSDKChatActivity.INTENT_ORIENTATION, orientation);
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
                                        if ( isGoWebView ) {
                                            Intent intent = new Intent( context, IBotSDKChatActivity.class );
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra( IBotSDKChatActivity.INTENT_API_URL, url );
                                            if ( orientation != -100 )
                                                intent.putExtra(IBotSDKChatActivity.INTENT_ORIENTATION, orientation);
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

    public void setChatActivityOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void showIBotButton(final Context context, boolean isShow, final boolean isDraggable, int type, int animationType, ViewGroup view) {
        if  (isShow && !TextUtils.isEmpty(apiKey)) {
            if ( view != null )
                view.removeAllViews();
            button = new IBotChatButton(context, apiKey, type, animationType, view, this);
            view.setOnClickListener(null);
            view.setOnTouchListener(null);
            moveX = 0f;
            moveY = 0f;
            view.addView(button);
            gestureDetector = new GestureDetector(context, new SingleTapConfirm());
            Log.e("TAG", "isDraggable :: " + isDraggable);
            if ( isDraggable ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTranslationZ(90f);
                }
                view.setOnTouchListener(new ViewGroup.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.e("TAG", "onTouch");
                        if ( gestureDetector.onTouchEvent(event) ) {
                            Log.e("TAG", "CLICK OCCURRED");
                            goIBotChat();
                            return true;
                        } else {
                            Log.e("TAG", "DRAG OCCURRED");
                            if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                                moveX = v.getX() - event.getRawX();
                                moveY = v.getY() - event.getRawY();
                            } else if ( event.getAction() == MotionEvent.ACTION_MOVE ) {
                                v.animate() .x(event.getRawX() + moveX) .y(event.getRawY() + moveY) .setDuration(0) .start();
                            }
                            return true;
                        }
                    }
                });
            } else {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goIBotChat();
                    }
                });
            }
            initSDK(context, apiKey);
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

    public class DownloadImageAsyncTask extends AsyncTask<String, Void, Boolean> {
        public boolean type = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String sType = params[0];
            String path = params[1];
            if (TYPE_ICON.equals(sType) ) type = true; else type = false;

            return IBotDownloadImage.DownloadImage(context, path, apiKey, type);
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if ( type ) isIconDownloaded = true; else isCloseDownloaded = true;
            if ( isIconDownloaded && isCloseDownloaded ) {
                IBotAppPreferences.setLong(context, IBotAppPreferences.IBOT_REG_DATE + "_" + apiKey, regDate);
                handler.sendEmptyMessage(SET_RESOURCE);
            } else
                new DownloadImageAsyncTask().execute(TYPE_CLOSE, closePath);
        }
    }

    private void saveNewResources(File iconFile, File closeFile) {
        if ( iconFile.exists() )
            iconFile.delete();
        if ( closeFile.exists() )
            closeFile.delete();
        new DownloadImageAsyncTask().execute(TYPE_ICON, iconPath);
        IBotAppPreferences.setString(context, IBotAppPreferences.IBOT_BUTTON_BG_COLOR + "_" + apiKey, bgColor);
        IBotAppPreferences.setString(context, IBotAppPreferences.IBOT_TEXT_COLOR + "_" + apiKey, textColor);
        IBotAppPreferences.setString(context, IBotAppPreferences.IBOT_TEXT + "_" + apiKey, textStr);
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }
}
