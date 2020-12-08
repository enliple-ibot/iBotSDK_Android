package com.enliple.ibotproject;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;

import com.enliple.ibotsdk.IBotSDK;
import com.enliple.ibotsdk.widget.IBotChatButton;
import com.enliple.ibotsdk.widget.IBotChatButtonTypeA;

public class MainActivity extends AppCompatActivity {
    private static final int TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON = 0;
    private static final int TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON = 1;
    private static final int TYPE_NON_EXPANDABLE_BUTTON = 2;
    private static final int WEBVIEW = 0;
    private static final int BROWSER = 1;
    private static final int DEFAULT = 0;
    private static final int ORIENTATION_PORTRAIT = 1;
    private static final int ORIENTATION_LANDSCAPE = 2;
    private static final int DRAGGABLE = 0;
    private static final int FIXED = 1;
    private AppCompatSpinner openSpinner, typeSpinner, orientationSpinner, draggableSpinner, animateSpinner;
    private AppCompatEditText editColor;
    private Button btnShow;
    private RelativeLayout btnLayer;
    private RelativeLayout root;
    private int type = IBotChatButton.TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON;
    private int orientation = DEFAULT;
    private int draggable = DRAGGABLE;
    private int open = WEBVIEW;
    private IBotSDK sdk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setSdk();

        final String[] openData = getResources().getStringArray(R.array.how_to_open);
        final String[] typeData = getResources().getStringArray(R.array.type);
        final String[] orientationData = getResources().getStringArray(R.array.orientation);
        final String[] draggableData = getResources().getStringArray(R.array.draggable);
        final String[] animateData = getResources().getStringArray(R.array.animate);
        ArrayAdapter<String> openAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, openData);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, typeData);
        ArrayAdapter<String> orientationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, orientationData);
        ArrayAdapter<String> draggableAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, draggableData);
        ArrayAdapter<String> animateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, animateData);

        openSpinner.setAdapter(openAdapter);
        typeSpinner.setAdapter(typeAdapter);
        orientationSpinner.setAdapter(orientationAdapter);
        draggableSpinner.setAdapter(draggableAdapter);
        animateSpinner.setAdapter(animateAdapter);

        openSpinner.setSelection(WEBVIEW);
        typeSpinner.setSelection(TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON);
        orientationSpinner.setSelection(DEFAULT);
        draggableSpinner.setSelection(DRAGGABLE);
        animateSpinner.setSelection(0);

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

        draggableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                draggable = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSdk();
            }
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
        typeSpinner = findViewById(R.id.typeSpinner);
        orientationSpinner = findViewById(R.id.orientationSpinner);
        draggableSpinner = findViewById(R.id.draggableSpinner);
        animateSpinner = findViewById(R.id.animateSpinner);
        editColor = findViewById(R.id.editColor);
        btnShow = findViewById(R.id.btnShow);
        btnLayer = findViewById(R.id.btnLayer);

        root = findViewById(R.id.root);
    }

    private void setSdk() {
        // callback이 필요한 경우
        /**
        sdk = new IBotSDK(MainActivity.this, ListActivity.API_KEY, new IBotSDK.CallbackListener() {
            @Override
            public void onCallback(String str) {
                // 채팅창으로부터 넘어오는 값 처리부
            }
        });
        **/
        // callback이 필요 없는 경우
        sdk = new IBotSDK(MainActivity.this, ListActivity.API_KEY);

        if ( open == BROWSER ) {
            sdk.openIBotWithBrowser(); // callback을 받아야할 경우 설정하면 안됨
        }

        if ( orientation == ORIENTATION_PORTRAIT )
            sdk.setChatActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else if ( orientation == ORIENTATION_LANDSCAPE )
            sdk.setChatActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        boolean isDraggable = true;
        if ( draggable == FIXED )
            isDraggable = false;

        String buttonBaseColor = editColor.getText().toString();

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)btnLayer.getLayoutParams();
        if ( type == IBotChatButtonTypeA.TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON )
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        else if ( type == IBotChatButtonTypeA.TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON )
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        else
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        btnLayer.setLayoutParams(params);

        sdk.showIBotButton(MainActivity.this, true, isDraggable, type, buttonBaseColor, btnLayer);
    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
