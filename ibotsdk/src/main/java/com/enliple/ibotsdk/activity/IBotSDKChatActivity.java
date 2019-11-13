package com.enliple.ibotsdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
                            if ( TextUtils.isEmpty(uid) ) {
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
                                                    System.out.println("get info failed");
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

        WebChromeClient webChromeClient = new WebChromeClient();
        WebSettings settings = webView.getSettings();
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                Log.e("TAG", "*************** url :: " + url);
                if ( url != null ) {
                    if ( url.startsWith("http://") || url.startsWith("https://") )
                        webView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    else if ( url.startsWith("tel:") )
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

        webView.addJavascriptInterface(new IBotJavascriptInterface(), IBOT_JAVASCRIPT_NAME);
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( loadUrl != null && !TextUtils.isEmpty(loadUrl) )
            webView.loadUrl(loadUrl);
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    public void onBackPressed() {
        if ( webView != null && webView.canGoBack() )
            webView.goBack();
        else
            finish();
    }

    public class IBotJavascriptInterface {
        @JavascriptInterface
        public void onAppViewClose() {
            finish();
        }
    }
}
