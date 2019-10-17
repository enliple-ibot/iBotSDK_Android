package com.enliple.ibotsdk;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.enliple.ibotsdk.activity.IBotSDKChatActivity;
import com.enliple.ibotsdk.model.IBotButtonAttribute;
import com.enliple.ibotsdk.network.IBotNetworkTask;
import com.enliple.ibotsdk.widget.IBotChatButton;

public class IBotSDK {
    public static IBotSDK instance = new IBotSDK();
    public static String apiKey;

    public void initSDK(String mallId) {
        this.apiKey = mallId;
    }
    public void goIBotChat(final Context context) {
        Log.e("TAG", "packageName :: " + context.getPackageName());
        new IBotNetworkTask(context).getChatUrl(apiKey, new IBotNetworkTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                Log.e("TAG", "result :: " + result);
                if ( result ) {
                    if ( apiKey != null && !TextUtils.isEmpty(apiKey) ) {
                        Intent intent = new Intent( context, IBotSDKChatActivity.class );
                        intent.putExtra( IBotSDKChatActivity.INTENT_API_KEY, apiKey );
                        context.startActivity(intent);
                    } else {
                        System.out.println("init sdk failed. not exist apiKey");
                    }
                }
            }
        });
    }
    public void showIBotButton(Context context, boolean isShow, ViewGroup view, IBotButtonAttribute attr) {
        if ( isShow ) {
            if ( attr == null )
                attr = new IBotButtonAttribute();
            IBotChatButton button = new IBotChatButton(context, attr);
            view.addView(button);
        }
    }
}
