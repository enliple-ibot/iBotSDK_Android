package com.enliple.ibotproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.enliple.ibotsdk.ActivityPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private WebView webView = null;
    private FloatingActionButton actionButton = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        actionButton = findViewById(R.id.actionButton);

        WebChromeClient chromeClient = new WebChromeClient();
        WebViewClient webViewClient = new WebViewClient();
        WebSettings settings = webView.getSettings();

        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(webViewClient);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

//        webView.loadUrl("https://superbeeracing.com");
//        webView.loadUrl("http://www.naver.com");

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityPresenter.shared.presentChatbotActivity(MainActivity.this, "8");
            }
        });
    }
}
