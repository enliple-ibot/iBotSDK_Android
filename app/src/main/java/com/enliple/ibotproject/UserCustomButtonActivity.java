package com.enliple.ibotproject;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import com.enliple.ibotsdk.IBotSDK;

public class UserCustomButtonActivity extends Activity {
    private static final int WEBVIEW = 0;
    private static final int BROWSER = 1;
    private static final int DEFAULT = 0;
    private static final int ORIENTATION_PORTRAIT = 1;
    private static final int ORIENTATION_LANDSCAPE = 2;
    private int orientation = DEFAULT;
    private int open = WEBVIEW;
    private AppCompatSpinner openSpinner, orientationSpinner;
    private IBotSDK sdk;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_custom_button);

        initViews();

        final String[] openData = getResources().getStringArray(R.array.how_to_open);
        final String[] orientationData = getResources().getStringArray(R.array.orientation);
        ArrayAdapter<String> openAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, openData);
        ArrayAdapter<String> orientationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, orientationData);
        openSpinner.setAdapter(openAdapter);
        orientationSpinner.setAdapter(orientationAdapter);
        openSpinner.setSelection(WEBVIEW);
        orientationSpinner.setSelection(DEFAULT);

        openSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                open = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        orientationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orientation = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void onDestroy() {
        if ( sdk != null )
            sdk.unregisterReceiver();
        super.onDestroy();
    }

    private void initViews() {
        openSpinner = findViewById(R.id.openSpinner);
        orientationSpinner = findViewById(R.id.orientationSpinner);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButton();
            }
        });
    }

    private void setButton() {
        // callback이 필요한 경우
        /**
        sdk = new IBotSDK(UserCustomButtonActivity.this, ListActivity.API_KEY, new IBotSDK.CallbackListener() {
            @Override
            public void onCallback(String str) {
                // 채팅창으로부터 넘어오는 값 처리부
            }
        });
         **/
        // callback이 필요 없는 경우
        sdk = new IBotSDK(UserCustomButtonActivity.this, ListActivity.API_KEY);

        if ( open == BROWSER ) {
            sdk.openIBotWithBrowser(); // callback을 받아야할 경우 설정하면 안됨
        }
        if ( orientation == ORIENTATION_PORTRAIT )
            sdk.setChatActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else if ( orientation == ORIENTATION_LANDSCAPE )
            sdk.setChatActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        sdk.initSDKForCustomButton();
    }
}
