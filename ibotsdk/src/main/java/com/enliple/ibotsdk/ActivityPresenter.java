package com.enliple.ibotsdk;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.enliple.ibotsdk.activity.IBotSDKChatActivity;
import com.enliple.ibotsdk.network.IBotNetworkTask;

public class ActivityPresenter {
    public static ActivityPresenter shared = new ActivityPresenter();
    public void presentChatbotActivity(final Context context, final String mallId ) {
        Log.e("TAG", "packageName :: " + context.getPackageName());
        new IBotNetworkTask(context).getChatUrl(mallId, new IBotNetworkTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                Log.e("TAG", "result :: " + result);
                if ( result ) {
                    if ( mallId != null && !TextUtils.isEmpty(mallId) ) {
                        Intent intent = new Intent( context, IBotSDKChatActivity.class );
                        intent.putExtra( IBotSDKChatActivity.INTENT_KEY_MALL_ID, mallId );
                        context.startActivity(intent);
                    }
                }
            }
        });
    }
}
