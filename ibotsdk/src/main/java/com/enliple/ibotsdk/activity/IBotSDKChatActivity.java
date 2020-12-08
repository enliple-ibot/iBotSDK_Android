package com.enliple.ibotsdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.enliple.ibotsdk.IBotSDK;
import com.enliple.ibotsdk.R;
import com.enliple.ibotsdk.common.IBotKey;
import com.enliple.ibotsdk.network.IBotNetworkAsyncTask;

import org.json.JSONObject;

public class IBotSDKChatActivity extends Activity {
    public static final String IBOT_JAVASCRIPT_NAME = "iBotAppHandler";
    public static final String INTENT_API_URL = "INTENT_API_URL";
    public static final String INTENT_ORIENTATION = "INTENT_ORIENTATION";
    private static final int SEND_COOKIE = 0;
    private WebView webView = null;

    private String loadUrl = "";
    private String finishedUrl = "";
    private String uid = "";
    private StringBuilder builder;
    private Listener listener;
    public interface Listener {
        void cookieDeleted();
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if ( msg.what == SEND_COOKIE ) {
                CookieManager cookieManager = CookieManager.getInstance();
                String cookies = cookieManager.getCookie(finishedUrl);
                if ( cookies != null ) {
                    String[] arrCookie = cookies.split(";");
                    for ( String arg : arrCookie ) {
                        if ( arg.contains(IBotKey.UID) ) {
                            String[] value = arg.split("=");
                            uid = value[1];
                            if ( TextUtils.isEmpty(uid) || "''".equals(uid) ) {
                                Message message = new Message();
                                message.what = SEND_COOKIE;
                                handler.sendMessageDelayed(message, 1000);
                            } else {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(IBotKey.UID, uid);
                                    JSONObject dataObject = new JSONObject();
                                    dataObject.put(IBotKey.UUID, IBotSDK.getUUID(getApplicationContext()));
                                    dataObject.put(IBotKey.OS_VERSION, IBotSDK.getOSVersion());
                                    dataObject.put(IBotKey.OS_TYPE, "Android");
                                    dataObject.put(IBotKey.SDK_VERSION, IBotSDK.getSDKVersion());
                                    dataObject.put(IBotKey.DEVICE, IBotSDK.getModel());
                                    object.put(IBotKey.DATA, dataObject);

                                    new IBotNetworkAsyncTask().sendInfos(object.toString(), new IBotNetworkAsyncTask.OnDefaultObjectCallbackListener() {
                                        @Override
                                        public void onResponse(boolean rt, Object obj) {
                                            if ( rt ) {
                                                try {
                                                    JSONObject object = new JSONObject(obj.toString());
                                                    boolean result = object.optBoolean("result");
                                                    if ( !result )
                                                        System.out.println("get info response false");
                                                    else
                                                        System.out.println("get info result " + obj.toString());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } else {
                    Message message = new Message();
                    message.what = SEND_COOKIE;
                    handler.sendMessageDelayed(message, 1000);
                }
            }
        }
    };
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibot_chat);

        Intent intent = getIntent();
        loadUrl = intent.getStringExtra(INTENT_API_URL);
        int orientation = intent.getIntExtra(INTENT_ORIENTATION, -100);
        if ( orientation != -100 ) {
            setRequestedOrientation(orientation);
        }

        webView = findViewById(R.id.webView);

        builder = new StringBuilder();
        WebSettings settings = webView.getSettings();
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String message = consoleMessage.message() + '\n' + consoleMessage.messageLevel() + '\n' + consoleMessage.sourceId();
                builder.append(message);
                builder.append("\n");
                return super.onConsoleMessage(consoleMessage);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if ( url != null ) {
                    if ( url.startsWith("http://") || url.startsWith("https://") ) {
                        if ( url.equals(loadUrl) ) {
                            if ( loadUrl != null && !TextUtils.isEmpty(loadUrl) ) {
                                webView.loadUrl(loadUrl);
                                return false;
                            }
                        } else {
                            webView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        }
                    } else if ( url.startsWith("tel:") )
                        webView.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                    else if ( url.startsWith("mailto:") )
                        webView.getContext().startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                    else
                        return false;
                    return true;
                } else
                    return false;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                if ( !TextUtils.isEmpty(loadUrl) ) {
                    if ( url.equals(loadUrl) ) {
                        if ( !url.equals(finishedUrl) ) {
                            finishedUrl = url;
                            Message message = new Message();
                            message.what = SEND_COOKIE;
                            handler.sendMessageDelayed(message, 1000);
                        }
                    }
                }
            }
        });
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false); // tts 재생되지 않아 tts 관련 설정
        }

        webView.addJavascriptInterface(new IBotJavascriptInterface(), IBOT_JAVASCRIPT_NAME);

//        deleteUID(new Listener() {
//            @Override
//            public void cookieDeleted() {
//                if ( loadUrl != null && !TextUtils.isEmpty(loadUrl) )
//                    webView.loadUrl(loadUrl);
//            }
//        });
        if ( loadUrl != null && !TextUtils.isEmpty(loadUrl) )
            webView.loadUrl(loadUrl);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // 채팅창 나갈 때는 handler message 모두 cancel
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onBackPressed() {
        finish();
    }

    public class IBotJavascriptInterface {
        @JavascriptInterface
        public void onAppViewClose() {
            finish();
        }

        @JavascriptInterface
        public void onAppSend(String str) {
            Intent intent = new Intent(IBotSDK.EVENT_CALLBACK);
            intent.putExtra(IBotSDK.KEY_CALLBACK, str);
            LocalBroadcastManager.getInstance(IBotSDKChatActivity.this).sendBroadcast(intent);
        }
    }

    private void deleteUID(Listener listener) {
        String cookieString = IBotKey.UID + "=''";
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(loadUrl, cookieString);
        if ( listener != null )
            listener.cookieDeleted();
    }
}
