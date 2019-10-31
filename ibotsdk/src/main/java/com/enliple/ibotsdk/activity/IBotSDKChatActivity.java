package com.enliple.ibotsdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.enliple.ibotsdk.IBotSDK;
import com.enliple.ibotsdk.R;
import com.enliple.ibotsdk.network.IBotNetworkAsyncTask;

import org.json.JSONObject;

public class IBotSDKChatActivity extends Activity {
    public static final String IBOT_JAVASCRIPT_NAME = "iBotAppHandler";
    public static final String INTENT_API_URL = "INTENT_API_URL";
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
                        if ( arg.contains("uid") ) {
                            String[] value = arg.split("=");
                            uid = value[1];
                            if ( TextUtils.isEmpty(uid) ) {
                                Message message = new Message();
                                message.what = SEND_COOKIE;
                                handler.sendMessageDelayed(message, 1000);
                            } else {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put("uid", uid);
                                    JSONObject dataObject = new JSONObject();
                                    dataObject.put("uuid", IBotSDK.getUUID(getApplicationContext()));
                                    dataObject.put("os_version", IBotSDK.getOSVersion());
                                    dataObject.put("os_type", "Android");
                                    dataObject.put("sdk_version", IBotSDK.getSDKVersion());
                                    dataObject.put("device", IBotSDK.getModel());
                                    object.put("data", dataObject);

                                    new IBotNetworkAsyncTask().sendInfos(object.toString(), new IBotNetworkAsyncTask.OnDefaultObjectCallbackListener() {
                                        @Override
                                        public void onResponse(boolean rt, Object obj) {
                                            if ( rt ) {
                                                try {
                                                    JSONObject object = new JSONObject(obj.toString());
                                                    boolean result = object.optBoolean("result");
                                                    if ( !result ) {
                                                        System.out.println("get info response false");
                                                    }
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

        webView = findViewById(R.id.webView);

        WebChromeClient webChromeClient = new WebChromeClient();
        WebSettings settings = webView.getSettings();
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if ( url != null ) {
                    //채팅창 내에서 외부 링크등을 통해 클릭 발생할 경우 webview 내 다른 페이지로 이동하는 것을 막기 위해
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

        if ( loadUrl != null && !TextUtils.isEmpty(loadUrl) )
            webView.loadUrl(loadUrl);
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
