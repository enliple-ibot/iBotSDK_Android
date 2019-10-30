package com.enliple.ibotproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.enliple.ibotsdk.IBotSDK;
import com.enliple.ibotsdk.widget.IBotChatButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private WebView webView = null;
    private FloatingActionButton actionButton = null;
    private LinearLayout buttonLayer;
    private IBotSDK sdk = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);
        actionButton = findViewById(R.id.actionButton);
        buttonLayer = findViewById(R.id.buttonLayer);
        sdk = new IBotSDK(getApplicationContext(), "205");
        sdk.showIBotButton(MainActivity.this, true, IBotChatButton.TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON, buttonLayer);

        WebChromeClient chromeClient = new WebChromeClient();
        WebViewClient webViewClient = new WebViewClient();
        WebSettings settings = webView.getSettings();

        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(webViewClient);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
    }
}
