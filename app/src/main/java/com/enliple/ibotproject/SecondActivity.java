package com.enliple.ibotproject;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.enliple.ibotsdk.IBotSDK;
import com.enliple.ibotsdk.widget.IBotChatButton;

public class SecondActivity extends AppCompatActivity {
    private LinearLayout buttonLayer;
    private IBotSDK sdk = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        buttonLayer = findViewById(R.id.buttonLayer);

        sdk = new IBotSDK(SecondActivity.this, "100");
        sdk.showIBotButton(SecondActivity.this, true, IBotChatButton.TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON, buttonLayer);
    }
}
