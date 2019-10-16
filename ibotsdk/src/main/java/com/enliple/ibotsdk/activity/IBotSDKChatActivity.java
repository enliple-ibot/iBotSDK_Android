package com.enliple.ibotsdk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.enliple.ibotsdk.R;
import com.enliple.ibotsdk.network.IBotURL;

public class IBotSDKChatActivity extends AppCompatActivity {

    public static final String INTENT_KEY_MALL_ID = "INTENT_KEY_MALL_ID";

    private WebView webView = null;

    private String mallId = null;
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibot_chat);

        Intent intent = getIntent();
        mallId = intent.getStringExtra(INTENT_KEY_MALL_ID);

        webView = findViewById(R.id.webView);

        WebChromeClient webChromeClient = new WebChromeClient();
        WebViewClient webViewClient = new WebViewClient();
        WebSettings settings = webView.getSettings();
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);
        settings.setJavaScriptEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if ( mallId != null && !TextUtils.isEmpty(mallId) )
            webView.loadUrl(IBotURL.CHAT_URL + mallId);
    }
}
