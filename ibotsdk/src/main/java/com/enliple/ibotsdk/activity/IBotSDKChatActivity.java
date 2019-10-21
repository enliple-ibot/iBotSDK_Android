package com.enliple.ibotsdk.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.enliple.ibotsdk.R;
import com.enliple.ibotsdk.network.IBotURL;

public class IBotSDKChatActivity extends AppCompatActivity {
    public static final String IBOT_JAVASCRIPT_NAME = "iBotAppHandler";
    public static final String INTENT_API_KEY = "INTENT_API_KEY";

    private WebView webView = null;

    private String mallId = null;
    private String loadUrl = "";
    private String finishedUrl = "";
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibot_chat);

        Intent intent = getIntent();
        mallId = intent.getStringExtra(INTENT_API_KEY);

        webView = findViewById(R.id.webView);

        WebChromeClient webChromeClient = new WebChromeClient();
        WebSettings settings = webView.getSettings();
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                //채팅창 내에서 외부 링크등을 통해 클릭 발생할 경우 webview 내 다른 페이지로 이동하는 것을 막기 위해
                if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    webView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                if ( !TextUtils.isEmpty(loadUrl) ) {
                    if ( url.equals(loadUrl) ) {
                        if ( !url.equals(finishedUrl) ) {
                            finishedUrl = url;
                            webView.loadUrl("javascript:window.close()");
                        }
                    }
                }
            }
        });
        settings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new IBotJavascriptInterface(), IBOT_JAVASCRIPT_NAME);

        if ( mallId != null && !TextUtils.isEmpty(mallId) ) {
            loadUrl = IBotURL.CHAT_URL_TEST + mallId;
            webView.loadUrl(loadUrl);
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//
//    }

    public class IBotJavascriptInterface {
        @JavascriptInterface
        public void onAppViewClose() {
            System.out.println("onAppViewClose called");
            finish();
        }
    }
}
