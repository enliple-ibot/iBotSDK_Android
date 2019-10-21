package com.enliple.ibotproject;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.enliple.ibotsdk.IBotSDK;
import com.enliple.ibotsdk.model.IBotButtonAttribute;
import com.enliple.ibotsdk.widget.IBotChatButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private WebView webView = null;
    private FloatingActionButton actionButton = null;
    private LinearLayout buttonLayer;
    private IBotChatButton chatButton;
    private boolean useButtonShow = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IBotSDK.instance.initSDK("205");
        webView = findViewById(R.id.webView);
        actionButton = findViewById(R.id.actionButton);
        buttonLayer = findViewById(R.id.buttonLayer);

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
                IBotSDK.instance.goIBotChat(MainActivity.this);
            }
        });
        if ( useButtonShow ) {
            IBotButtonAttribute attr = new IBotButtonAttribute();
            attr.setButtonBg(R.drawable.showbot_icon);
            attr.setBarBgColor((R.color.bar_bg));
            attr.setBarText(R.string.bot_msg);
            attr.setBarTextColor(R.color.white);
            attr.setBarTextSize(16);
            attr.setSize(60);
            attr.setCloseImage(R.drawable.ico_close);
            attr.setType(IBotChatButton.TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON);
            IBotSDK.instance.showIBotButton(MainActivity.this, true, buttonLayer, attr);
        } else {
            IBotButtonAttribute attr = new IBotButtonAttribute();
            attr.setButtonBg(R.drawable.showbot_icon);
            attr.setBarBgColor((R.color.bar_bg));
            attr.setBarText(R.string.bot_msg);
            attr.setBarTextColor(R.color.white);
            attr.setBarTextSize(16);
            attr.setSize(60);
            attr.setCloseImage(R.drawable.ico_close);
            attr.setType(IBotChatButton.TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON);

            chatButton = new IBotChatButton(MainActivity.this, attr);

            buttonLayer.addView(chatButton);
        }
    }
}
