package com.enliple.ibotproject;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.enliple.ibotsdk.IBotSDK;
import com.enliple.ibotsdk.widget.IBotChatButton;

public class MainActivity extends AppCompatActivity {
    private static final int TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON = 0;
    private static final int TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON = 1;
    private static final int TYPE_NON_EXPANDABLE_BUTTON = 2;
    private static final int WEBVIEW = 0;
    private static final int BROWSER = 1;
    private static final int DEFAULT = 0;
    private static final int ORIENTATION_PORTRAIT = 1;
    private static final int ORIENTATION_LANDSCAPE = 2;

    private AppCompatSpinner openSpinner, typeSpinner, orientationSpinner;
    private AppCompatButton btnShow;
    private RelativeLayout btnLayer;
    private int type = IBotChatButton.TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON;
    private int orientation = DEFAULT;
    private int open = WEBVIEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setSdk();

        final String[] openData = getResources().getStringArray(R.array.how_to_open);
        final String[] typeData = getResources().getStringArray(R.array.type);
        final String[] orientationData = getResources().getStringArray(R.array.orientation);

        ArrayAdapter<String> openAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, openData);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, typeData);
        ArrayAdapter<String> orientationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, orientationData);

        openSpinner.setAdapter(openAdapter);
        typeSpinner.setAdapter(typeAdapter);
        orientationSpinner.setAdapter(orientationAdapter);

        openSpinner.setSelection(TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON);
        typeSpinner.setSelection(WEBVIEW);
        orientationSpinner.setSelection(DEFAULT);

        openSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                open = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( position == TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON ) {
                    type = IBotChatButton.TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON;
                } else if ( position == TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON ) {
                    type = IBotChatButton.TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON;
                } else if ( position == TYPE_NON_EXPANDABLE_BUTTON ) {
                    type = IBotChatButton.TYPE_NON_EXPANDABLE_BUTTON;
                }
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

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSdk();
                Toast.makeText(MainActivity.this, getString(R.string.set_button), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        openSpinner = findViewById(R.id.openSpinner);
        typeSpinner = findViewById(R.id.typeSpinner);
        orientationSpinner = findViewById(R.id.orientationSpinner);
        btnShow = findViewById(R.id.btnShow);
        btnLayer = findViewById(R.id.btnLayer);
    }

    private void setSdk() {
        IBotSDK sdk = new IBotSDK(MainActivity.this, "발급받은 API KEY");

        if ( open == BROWSER ) {
            sdk.openIBotWithBrowser();
        }

        if ( orientation == ORIENTATION_PORTRAIT )
            sdk.setChatActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else if ( orientation == ORIENTATION_LANDSCAPE )
            sdk.setChatActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sdk.showIBotButton(MainActivity.this, true, type, btnLayer);
    }
}
