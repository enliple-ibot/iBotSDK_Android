package com.enliple.ibotsdk.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import androidx.annotation.RequiresApi;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.enliple.ibotsdk.IBotSDK;
import com.enliple.ibotsdk.R;
import com.enliple.ibotsdk.common.IBotAppPreferences;
import com.enliple.ibotsdk.common.IBotDownloadImage;
import com.enliple.ibotsdk.gif.IBotGifAnimationDrawable;
import com.enliple.ibotsdk.network.IBotNetworkAsyncTask;
import com.enliple.ibotsdk.shadow.IBotOutlineProvider;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class IBotChatButtonTypeA extends FrameLayout {
    public static final int TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON = 0; // button은 화면 우측에 위치하고 왼쪽으로 expanding area가 노출됨
    public static final int TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON = 1; // button은 화면 좌측에 위치하고 오른쪾으로 expanding area가 노출됨
    public static final int TYPE_NON_EXPANDABLE_BUTTON = 2; // expanding area가 노출되지 않으며 버튼 위치는 어느곳에든 위치할 수 있음
    public static final int DEFAULT_FRAME_HEIGHT = 90;
    public static final int DEFAULT_LAYER_HEIGHT = 90;
    public static final int DEFAULT_BTN_WIDTH = 70;
    public static final int DEFAULT_BTN_HEIGHT = 70;
    public static final int DEFAULT_BAR_HEIGHT = 50;
    public static final int DEFAULT_MSG_WIDTH = 40;
    public static final int DEFAULT_MSG_HEIGHT = 36;
    public static final int DEFAULT_SHADOW_MARGIN = 10;
    //    public static final int DEFAULT_SIZE = 60; // default ChatBotButton size
    public static final int DEFAULT_TEXT_SIZE = 14; // expanding area에 있는 문구의 default size
    public static final String ANIMATION_NONE = "ANIMATE_NONE";
    public static final String ANIMATION_FADE_IN = "ANIMATE_0000";
    public static final String ANIMATION_RAISE_UP = "ANIMATE_0001";
    public static final String ANIMATION_FLICKER = "ANIMATE_0002";
    public static final String ANIMATION_ROTATE = "ANIMATE_0003";
    public static final String ANIMATION_SPRING = "ANIMATE_0004";
    public static final String ANIMATION_MOVE_LEFT_TO_RIGHT = "ANIMATE_0005";

    private Context context;
    private IBotSDK sdk;
    private ViewGroup view;
    private FrameLayout frame;
    private RelativeLayout layer;
    private HorizontalScrollView root;
    private RelativeLayout buttonLayer;
    private TextView textExplain;
    private ImageView btnBackground, buttonBg, msgIcon;
    private IBotRoundLayout maskLayer;

    private int type = TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON;
    private String animationType = ANIMATION_FADE_IN;
    public static boolean useOriginImage = true;
    private GradientDrawable buttonBarBackground;
    private GradientDrawable buttonBackground;
    private Drawable bBgImage;
    private Drawable cBtnImage;
    private int barBg;
    private int btnBgColor;
    private int barTextColor;
    private int frameHeight = DEFAULT_FRAME_HEIGHT;
    private int btnWidth = DEFAULT_BTN_WIDTH;
    private int layerHeight = DEFAULT_LAYER_HEIGHT;
    private int btnHeight = DEFAULT_BAR_HEIGHT;
    private int barHeight = DEFAULT_BAR_HEIGHT;
    private int msgWidth = DEFAULT_MSG_WIDTH;
    private int msgHeight = DEFAULT_MSG_HEIGHT;
    private int msgMargin = 0;
    private int shadowMargin = 0;
    //    private int sizeHeight = DEFAULT_SIZE;
//    private int sizeWidth = DEFAULT_SIZE;
    private int barTextSize = DEFAULT_TEXT_SIZE;
    private float radius;
    private String barText;
    private String apiKey = null;

    private int scrollMax;
    private int scrollPosition =	0;
    private TimerTask timerTask;
    private Timer timer =	null;
    private TimerTask eTimerTask;
    private Timer eTimer =	null;
    private int translationZ = 0;
    public IBotChatButtonTypeA(Context context, String apiKey, int type, String bgColor, ViewGroup view, IBotSDK sdk) {
        super(context);
        this.context = context;
        this.type = type;
        this.view = view;
        this.sdk = sdk;
        useOriginImage = false;
        setApiKey(apiKey);
        initViews();
    }

    public IBotChatButtonTypeA(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IBotChatButtonTypeA(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initViews() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) context.getSystemService(infService);
        View v = li.inflate(R.layout.ibot_chat_button_type_a, this, false);
        addView(v);

        frame = findViewById(R.id.frame);
        layer = findViewById(R.id.layer);
        root = findViewById(R.id.root);
        buttonLayer = findViewById(R.id.buttonLayer);
        textExplain = findViewById(R.id.textExplain);
        btnBackground = findViewById(R.id.btnBackground);
        buttonBg = findViewById(R.id.buttonBg);
        maskLayer = findViewById(R.id.maskLayer);
        msgIcon = findViewById(R.id.msgIcon);

        initImage(btnBackground);
        initImage(buttonBg);

        bBgImage = context.getResources().getDrawable(R.drawable.ibot_icon);
        cBtnImage = context.getResources().getDrawable(R.drawable.ibot_close_white_ico);
        barBg = context.getResources().getColor(R.color.ibot_bar_background);
        btnBgColor = context.getResources().getColor(R.color.ibot_bar_background);
        barTextColor = context.getResources().getColor( R.color.ibot_text_color);
        barText = context.getResources().getString(R.string.hello_ibot);
        barTextSize = DEFAULT_TEXT_SIZE;
        frameHeight = dpToPx(DEFAULT_FRAME_HEIGHT);
        btnHeight = dpToPx(DEFAULT_BTN_HEIGHT);
        btnWidth = btnHeight;
        layerHeight = dpToPx(DEFAULT_LAYER_HEIGHT);
        barHeight = dpToPx(DEFAULT_BAR_HEIGHT);
        msgWidth = dpToPx(DEFAULT_MSG_WIDTH);
        msgHeight = dpToPx(DEFAULT_MSG_HEIGHT);
        shadowMargin = dpToPx(DEFAULT_SHADOW_MARGIN);
        msgMargin = ((btnWidth - msgWidth) / 2) + shadowMargin;
        radius = barHeight / 2;
        String iconPath = getImageFilePath(true);
        String closePath = getImageFilePath(false);
        File iconFile = null;
        File closeFile = null;
        if ( !TextUtils.isEmpty(iconPath) ) {
            iconFile = new File(iconPath);
        }

        if ( !TextUtils.isEmpty(closePath) ) {
            closeFile = new File(closePath);
        }

        if ( iconFile != null && iconFile.exists() ) {
            Bitmap iconBitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
            bBgImage = new BitmapDrawable(context.getResources(), iconBitmap);
//            bBgImage = RoundedBitmapDrawableFactory.create(getResources(),iconBitmap);
//            ((RoundedBitmapDrawable)bBgImage).setCircular(true);
        }

        if ( closeFile != null && closeFile.exists() ) {
            Bitmap closeBitmap = BitmapFactory.decodeFile(closeFile.getAbsolutePath());
            cBtnImage = new BitmapDrawable(context.getResources(), closeBitmap);
        }

        String bgColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_BAR_BG_COLOR + "_" + apiKey);
        if ( !TextUtils.isEmpty(bgColor) )
            barBg = Color.parseColor(bgColor);

        String buttonBgColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_BUTTON_BG_COLOR + "_" + apiKey);
        if ( !TextUtils.isEmpty(buttonBgColor) )
            btnBgColor = Color.parseColor(buttonBgColor);

        String textColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_TEXT_COLOR + "_" + apiKey);
        if ( !TextUtils.isEmpty(textColor) )
            barTextColor = Color.parseColor(textColor);

        String text = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_TEXT + "_" + apiKey);
        if ( !TextUtils.isEmpty(text) )
            barText = text;
        String animType = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_ANIMATION_TYPE + "_" + apiKey);
        if ( !TextUtils.isEmpty(animType) )
            animationType = animType;
//        Bitmap bitmapBgImage = ((BitmapDrawable) bBgImage).getBitmap();
//        int bBgImageWidth = bitmapBgImage.getWidth();
//        int bBgImageHeight = bitmapBgImage.getHeight();
//        btnWidth = (bBgImageWidth * DEFAULT_BTN_HEIGHT) / bBgImageHeight;
//        btnWidth = dpToPx(btnWidth);

        buttonBackground = new GradientDrawable();
        buttonBackground.setShape(GradientDrawable.OVAL);
        buttonBackground.setColor(btnBgColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btnBackground.setBackground(buttonBackground);
        } else {
            btnBackground.setBackgroundDrawable(buttonBackground);
        }

        if ( type == TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON ) {
            buttonBarBackground = new GradientDrawable();
            buttonBarBackground.setShape(GradientDrawable.RECTANGLE);
            buttonBarBackground.setCornerRadii(new float[] {radius, radius, 0f, 0f, 0f, 0f, radius, radius});
            buttonBarBackground.setColor(barBg);

            RelativeLayout.LayoutParams buttonLayerParams = (RelativeLayout.LayoutParams)buttonLayer.getLayoutParams();
            buttonLayerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            buttonLayer.setLayoutParams(buttonLayerParams);

            RelativeLayout.LayoutParams buttonBgParams = (RelativeLayout.LayoutParams)btnBackground.getLayoutParams();
            buttonBgParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            buttonBgParams.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
            buttonBgParams.width = btnWidth;
            buttonBgParams.height = btnHeight;
            btnBackground.setLayoutParams(buttonBgParams);

            RelativeLayout.LayoutParams maskParam = (RelativeLayout.LayoutParams)maskLayer.getLayoutParams();
            maskParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            maskParam.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
            maskParam.width = btnWidth;
            maskParam.height = btnHeight;
            maskLayer.setLayoutParams(maskParam);

            IBotRoundLayout.LayoutParams buttonParams = (IBotRoundLayout.LayoutParams)buttonBg.getLayoutParams();
            buttonParams.width = btnWidth;
            buttonParams.height = btnHeight;
            buttonBg.setLayoutParams(buttonParams);

            RelativeLayout.LayoutParams msgParams = (RelativeLayout.LayoutParams)msgIcon.getLayoutParams();
            msgParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            msgIcon.setLayoutParams(msgParams);

            RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
            rootParams.height = barHeight;
            rootParams.setMargins(dpToPx(10), dpToPx(10), (int)(btnWidth / 2) + shadowMargin, dpToPx(20));
            rootParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rootParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            root.setLayoutParams(rootParams);
            root.setPadding((int)radius, 0, (int)(btnWidth / 2) + dpToPx(25), 0);

            HorizontalScrollView.LayoutParams explainParams = (HorizontalScrollView.LayoutParams)textExplain.getLayoutParams();
            explainParams.gravity = Gravity.CENTER_VERTICAL;
            textExplain.setLayoutParams(explainParams);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                root.setBackground(buttonBarBackground);
            } else {
                root.setBackgroundDrawable(buttonBarBackground);
            }

            if ( !IBotDownloadImage.IsGifFile(iconPath) ) {
                setBackground(buttonBg, bBgImage);
            } else {
                setGifFile(buttonBg, iconFile);
            }
            textExplain.setText(barText);
            textExplain.setTextColor(barTextColor);
            textExplain.setTextSize(barTextSize / getResources().getConfiguration().fontScale);
        } else if ( type == TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON ) {
            buttonBarBackground = new GradientDrawable();
            buttonBarBackground.setShape(GradientDrawable.RECTANGLE);
            buttonBarBackground.setCornerRadii(new float[] {0f, 0f, radius, radius, radius, radius, 0f, 0f});
            buttonBarBackground.setColor(barBg);

            RelativeLayout.LayoutParams buttonLayerParams = (RelativeLayout.LayoutParams)buttonLayer.getLayoutParams();
            buttonLayerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            buttonLayer.setLayoutParams(buttonLayerParams);

            RelativeLayout.LayoutParams buttonBgParams = (RelativeLayout.LayoutParams)btnBackground.getLayoutParams();
            buttonBgParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            buttonBgParams.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
            buttonBgParams.width = btnWidth;
            buttonBgParams.height = btnHeight;
            btnBackground.setLayoutParams(buttonBgParams);

            RelativeLayout.LayoutParams maskParam = (RelativeLayout.LayoutParams)maskLayer.getLayoutParams();
            maskParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            maskParam.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
            maskParam.width = btnWidth;
            maskParam.height = btnHeight;
            maskLayer.setLayoutParams(maskParam);

            IBotRoundLayout.LayoutParams buttonParams = (IBotRoundLayout.LayoutParams)buttonBg.getLayoutParams();
            buttonParams.width = btnWidth;
            buttonParams.height = btnHeight;
            buttonBg.setLayoutParams(buttonParams);

            RelativeLayout.LayoutParams msgParams = (RelativeLayout.LayoutParams)msgIcon.getLayoutParams();
            msgParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            msgIcon.setLayoutParams(msgParams);

            RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
            rootParams.height = barHeight;
            rootParams.setMargins((int)(btnWidth / 2) + shadowMargin, dpToPx(10), dpToPx(10), dpToPx(20));
            rootParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rootParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            root.setLayoutParams(rootParams);
            root.setPadding((int)(btnWidth / 2) + dpToPx(25), 0, (int)radius, 0);

            HorizontalScrollView.LayoutParams explainParams = (HorizontalScrollView.LayoutParams)textExplain.getLayoutParams();
            explainParams.gravity = Gravity.CENTER_VERTICAL;
            textExplain.setLayoutParams(explainParams);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                root.setBackground(buttonBarBackground);
            } else {
                root.setBackgroundDrawable(buttonBarBackground);
            }

            if ( !IBotDownloadImage.IsGifFile(iconPath) ) {
                setBackground(buttonBg, bBgImage);
            } else {
                setGifFile(buttonBg, iconFile);
            }
            textExplain.setText(barText);
            textExplain.setTextColor(barTextColor);
            textExplain.setTextSize(barTextSize / getResources().getConfiguration().fontScale);
        } else {
            ViewGroup.LayoutParams frameParams = frame.getLayoutParams();
            frameParams.width = btnWidth + (shadowMargin * 2);
            frameParams.height = frameHeight;
            frame.setLayoutParams(frameParams);

//            ViewGroup.LayoutParams layerParams = layer.getLayoutParams();
//            FrameLayout.LayoutParams layerParams = new FrameLayout.LayoutParams(btnWidth + (shadowMargin * 2), btnHeight);
//            layerParams.gravity = Gravity.BOTTOM;
//            layer.setLayoutParams(layerParams);

            root.setVisibility(View.GONE);
//            shadowRoot.setVisibility(View.GONE);

            RelativeLayout.LayoutParams msgParams = (RelativeLayout.LayoutParams)msgIcon.getLayoutParams();
            msgParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            msgIcon.setLayoutParams(msgParams);

            RelativeLayout.LayoutParams buttonBgParams = (RelativeLayout.LayoutParams)btnBackground.getLayoutParams();
            buttonBgParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            buttonBgParams.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
            buttonBgParams.width = btnWidth;
            buttonBgParams.height = btnHeight;
            btnBackground.setLayoutParams(buttonBgParams);

            RelativeLayout.LayoutParams maskParam = (RelativeLayout.LayoutParams)maskLayer.getLayoutParams();
            maskParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            maskParam.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
            maskParam.width = btnWidth;
            maskParam.height = btnHeight;
            maskLayer.setLayoutParams(maskParam);

            IBotRoundLayout.LayoutParams buttonParams = (IBotRoundLayout.LayoutParams)buttonBg.getLayoutParams();
            buttonParams.width = btnWidth;
            buttonParams.height = btnHeight;
            buttonBg.setLayoutParams(buttonParams);

            if ( !IBotDownloadImage.IsGifFile(iconPath) ) {
                setBackground(buttonBg, bBgImage);
            } else {
                setGifFile(buttonBg, iconFile);
            }

        }
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    private void barAnimation() {
        if ( type != TYPE_NON_EXPANDABLE_BUTTON ) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    root.setVisibility(View.VISIBLE);
//                    shadowRoot.setVisibility(View.VISIBLE);
                    ValueAnimator mAnimator = slideAnimator(0, root.getWidth());
                    mAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {}

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            // expanding area 다 열리고 나서 text 문구 노출 시킴 (with. Alpha animation)
                            Animation animation = new AlphaAnimation(0, 1);
                            animation.setDuration(2000);
                            textExplain.setVisibility(View.VISIBLE);
                            textExplain.setAnimation(animation);
                            autoScrollExplain();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {}

                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    });
                    mAnimator.start();
                }
            }, 1);
        }
    }

    /**
     * expanding area 확장/축소 애니메이션
     * @param start animation 시작 시 크기
     * @param end animation 종료 시 크기
     * @return
     */
    private ValueAnimator slideAnimator(int start, final int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
                layoutParams.width = value;
                root.setLayoutParams(layoutParams);

                if ( value != 0 && value == end ) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            closeBar();
                        }
                    }, 4000);
                }
            }
        });
        return animator;
    }



    /**
     * 열려 있는 expanding area 닫음
     */
    private void closeBar() {
        int finalHeight = root.getWidth();
        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                root.setVisibility(View.GONE);
//                shadowRoot.setVisibility(View.GONE);
                LayoutParams layerParams = (LayoutParams)layer.getLayoutParams();
                layerParams.width = btnWidth + (shadowMargin * 2);
                layerParams.height = layerHeight;
                layerParams.gravity = Gravity.RIGHT;
                layer.setLayoutParams(layerParams);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        mAnimator.start();
    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        super.dispatchWindowFocusChanged(hasFocus);
        if( apiKey != null && hasFocus && getVisibility() == View.VISIBLE ) {
            new IBotNetworkAsyncTask().isAlivePackage(apiKey, new IBotNetworkAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {}
            });
        }
    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onReceived() {
        try {
            String iconFilePath = getImageFilePath(true);
            String closeFilePath = getImageFilePath(false);

            if ( !TextUtils.isEmpty(iconFilePath) ) {
                File iconFile = new File(iconFilePath);
                if ( iconFile.exists() ) {
                    Bitmap iconBitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
                    bBgImage = new BitmapDrawable(context.getResources(), iconBitmap);
//                    bBgImage = RoundedBitmapDrawableFactory.create(getResources(),iconBitmap);
//                    ((RoundedBitmapDrawable)bBgImage).setCircular(true);
//                    Bitmap bitmapBgImage = ((BitmapDrawable) bBgImage).getBitmap();
//                    int bBgImageWidth = bitmapBgImage.getWidth();
//                    int bBgImageHeight = bitmapBgImage.getHeight();
//                    btnWidth = (bBgImageWidth * DEFAULT_BTN_WIDTH) / bBgImageHeight;
//                    btnWidth = dpToPx(btnWidth);

                    msgMargin = ((btnWidth - msgWidth) / 2) + shadowMargin;
                    buttonBarBackground = new GradientDrawable();
                    buttonBarBackground.setShape(GradientDrawable.RECTANGLE);

                    buttonBackground = new GradientDrawable();
                    buttonBackground.setShape(GradientDrawable.OVAL);

                    if ( type == TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON ) {
                        RelativeLayout.LayoutParams buttonLayerParams = (RelativeLayout.LayoutParams)buttonLayer.getLayoutParams();
                        buttonLayerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        buttonLayer.setLayoutParams(buttonLayerParams);

                        RelativeLayout.LayoutParams buttonBgParams = (RelativeLayout.LayoutParams)btnBackground.getLayoutParams();
                        buttonBgParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        buttonBgParams.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
                        buttonBgParams.width = btnWidth;
                        buttonBgParams.height = btnHeight;
                        btnBackground.setLayoutParams(buttonBgParams);

                        RelativeLayout.LayoutParams maskParam = (RelativeLayout.LayoutParams)maskLayer.getLayoutParams();
                        maskParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        maskParam.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
                        maskParam.width = btnWidth;
                        maskParam.height = btnHeight;
                        maskLayer.setLayoutParams(maskParam);

                        IBotRoundLayout.LayoutParams buttonParams = (IBotRoundLayout.LayoutParams)buttonBg.getLayoutParams();
                        buttonParams.width = btnWidth;
                        buttonParams.height = btnHeight;
                        buttonBg.setLayoutParams(buttonParams);

                        RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
                        rootParams.height = barHeight;
                        rootParams.setMargins(dpToPx(20), dpToPx(10), (int)(btnWidth / 2), dpToPx(20));
                        rootParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        rootParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        root.setLayoutParams(rootParams);

                        root.setPadding((int)radius, 0, (int)(btnWidth / 2) + dpToPx(25), 0);
                        buttonBarBackground.setCornerRadii(new float[] {radius, radius, 0f, 0f, 0f, 0f, radius, radius});
                    } else if ( type == TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON ) {
                        RelativeLayout.LayoutParams buttonLayerParams = (RelativeLayout.LayoutParams)buttonLayer.getLayoutParams();
                        buttonLayerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        buttonLayer.setLayoutParams(buttonLayerParams);

                        RelativeLayout.LayoutParams buttonBgParams = (RelativeLayout.LayoutParams)btnBackground.getLayoutParams();
                        buttonBgParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        buttonBgParams.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
                        buttonBgParams.width = btnWidth;
                        buttonBgParams.height = btnHeight;
                        btnBackground.setLayoutParams(buttonBgParams);

                        RelativeLayout.LayoutParams maskParam = (RelativeLayout.LayoutParams)maskLayer.getLayoutParams();
                        maskParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        maskParam.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
                        maskParam.width = btnWidth;
                        maskParam.height = btnHeight;
                        maskLayer.setLayoutParams(maskParam);

                        IBotRoundLayout.LayoutParams buttonParams = (IBotRoundLayout.LayoutParams)buttonBg.getLayoutParams();
                        buttonParams.width = btnWidth;
                        buttonParams.height = btnHeight;
                        buttonBg.setLayoutParams(buttonParams);

                        RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
                        rootParams.height = barHeight;
                        rootParams.setMargins((int)(btnWidth / 2), dpToPx(10), dpToPx(20), dpToPx(20));
                        rootParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        rootParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        root.setLayoutParams(rootParams);
                        root.setPadding((int)(btnWidth / 2) + dpToPx(25), 0, (int)radius, 0);

                        buttonBarBackground.setCornerRadii(new float[] {0f, 0f, radius, radius, radius, radius, 0f, 0f});
                        //
//                        LayoutParams layerParams = (LayoutParams)layer.getLayoutParams();
//                        layerParams.height = layerHeight;
//                        layerParams.gravity = Gravity.LEFT|Gravity.BOTTOM;
//                        layer.setLayoutParams(layerParams);
//
//                        RelativeLayout.LayoutParams shadowParams = (RelativeLayout.LayoutParams)buttonShadow.getLayoutParams();
//                        shadowParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                        shadowParams.setMargins(shadowMargin, 0, 0, shadowMargin);
//                        shadowParams.width = btnWidth;
//                        shadowParams.height = btnHeight;
//                        buttonShadow.setLayoutParams(shadowParams);
//
//                        RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
//                        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                        buttonParams.setMargins(shadowMargin, 0, 0, shadowMargin);
//                        buttonParams.width = btnWidth;
//                        buttonParams.height = btnHeight;
//                        buttonBg.setLayoutParams(buttonParams);
//
//                        RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
//                        rootParams.height = barHeight;
//                        rootParams.setMargins((int)(btnWidth / 2), dpToPx(10), dpToPx(20), dpToPx(20));
//                        rootParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                        rootParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                        root.setLayoutParams(rootParams);
//                        root.setPadding((int)(btnWidth / 2) + dpToPx(25), 0, (int)radius, 0);
//
//                        LayoutParams msgParams = (LayoutParams)msgIcon.getLayoutParams();
//                        msgParams.width = msgWidth;
//                        msgParams.height = msgHeight;
//                        msgParams.gravity = Gravity.LEFT;
//                        msgParams.setMargins(msgMargin, 0, 0, 0);
//                        msgIcon.setLayoutParams(msgParams);
//
////                        textExplain.setPadding((int)(btnWidth / 2) + dpToPx(25), 0, 0, 0);
//                        buttonBarBackground.setCornerRadii(new float[] {0f, 0f, radius, radius, radius, radius, 0f, 0f});
                    } else {
                        ViewGroup.LayoutParams frameParams = frame.getLayoutParams();
                        frameParams.width = btnWidth + (shadowMargin * 2);
                        frameParams.height = frameHeight;
                        frame.setLayoutParams(frameParams);

                        RelativeLayout.LayoutParams msgParams = (RelativeLayout.LayoutParams)msgIcon.getLayoutParams();
                        msgParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        msgIcon.setLayoutParams(msgParams);

                        root.setVisibility(View.GONE);

                        RelativeLayout.LayoutParams buttonBgParams = (RelativeLayout.LayoutParams)btnBackground.getLayoutParams();
                        buttonBgParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        buttonBgParams.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
                        buttonBgParams.width = btnWidth;
                        buttonBgParams.height = btnHeight;
                        btnBackground.setLayoutParams(buttonBgParams);

                        RelativeLayout.LayoutParams maskParam = (RelativeLayout.LayoutParams)maskLayer.getLayoutParams();
                        maskParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        maskParam.setMargins(shadowMargin, shadowMargin, shadowMargin, shadowMargin);
                        maskParam.width = btnWidth;
                        maskParam.height = btnHeight;
                        maskLayer.setLayoutParams(maskParam);

                        IBotRoundLayout.LayoutParams buttonParams = (IBotRoundLayout.LayoutParams)buttonBg.getLayoutParams();
                        buttonParams.width = btnWidth;
                        buttonParams.height = btnHeight;
                        buttonBg.setLayoutParams(buttonParams);
//
//                        FrameLayout.LayoutParams layerParams = (FrameLayout.LayoutParams)layer.getLayoutParams();
//                        layerParams.width = btnWidth + dpToPx(20);
//                        layerParams.height = layerHeight;
//                        layerParams.gravity = Gravity.BOTTOM;
//                        layer.setLayoutParams(layerParams);
//
//                        LayoutParams msgParams = (LayoutParams)msgIcon.getLayoutParams();
//                        msgParams.width = msgWidth;
//                        msgParams.height = msgHeight;
//                        msgParams.setMargins(0, 0, msgMargin, 0);
//                        msgParams.gravity = Gravity.RIGHT;
//                        msgIcon.setLayoutParams(msgParams);
//
//                        root.setVisibility(View.GONE);
////                        shadowRoot.setVisibility(View.GONE);
//
//                        RelativeLayout.LayoutParams shadowParams = (RelativeLayout.LayoutParams)buttonShadow.getLayoutParams();
//                        shadowParams.width = btnWidth;
//                        shadowParams.height = btnHeight;
//                        shadowParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//                        shadowParams.setMargins(shadowMargin, 0, shadowMargin, shadowMargin);
//                        buttonShadow.setLayoutParams(shadowParams);
//
//                        RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
//                        buttonParams.width = btnWidth;
//                        buttonParams.height = btnHeight;
//                        buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//                        buttonParams.setMargins(shadowMargin, 0, shadowMargin, shadowMargin);
//                        buttonBg.setLayoutParams(buttonParams);
                    }

                    if ( !IBotDownloadImage.IsGifFile(iconFilePath) ) {
                        setBackground(buttonBg, bBgImage);
                    } else {
                        setGifFile(buttonBg, iconFile);
                    }
                }
            }

            String buttonBgColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_BUTTON_BG_COLOR + "_" + apiKey);
            if ( !TextUtils.isEmpty(buttonBgColor) )
                btnBgColor = Color.parseColor(buttonBgColor);
            buttonBackground.setColor(btnBgColor);

            String bgColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_BAR_BG_COLOR + "_" + apiKey);
            if ( !TextUtils.isEmpty(bgColor) )
                barBg = Color.parseColor(bgColor);
            buttonBarBackground.setColor(barBg);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                root.setBackground(buttonBarBackground);
            } else {
                root.setBackgroundDrawable(buttonBarBackground);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnBackground.setBackground(buttonBackground);
            } else {
                btnBackground.setBackgroundDrawable(buttonBackground);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                float cornerRadius = dpToPx(25);
                IBotOutlineProvider outlineProvider = new IBotOutlineProvider(cornerRadius, 0.96f, 1.32f, 0);
                root.setOutlineProvider(outlineProvider);
                root.setElevation(15);
            }

            String textColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_TEXT_COLOR + "_" + apiKey);
            if ( !TextUtils.isEmpty(textColor) ) {
                barTextColor = Color.parseColor(textColor);
                textExplain.setTextColor(barTextColor);
            }

            String text = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_TEXT + "_" + apiKey);
            if ( !TextUtils.isEmpty(text) ) {
                textExplain.setSelected(true);
                barText = text;
//                barText = "Once upon a time \n in a faraway land a young prince lived in a shinny castle. although everything his heart desire.";
//                barText = "Once upon a time in a faraway land a young prince lived in a shinny castle. although everything his heart desire.";
                textExplain.setText(barText);
            }

            String animType = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_ANIMATION_TYPE + "_" + apiKey);
            if ( !TextUtils.isEmpty(animType) ) {
                animationType = animType;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ( animationType.equals(ANIMATION_FADE_IN) ) {
            layer.setVisibility(View.VISIBLE);
            Animation animation = new AlphaAnimation(0, 1);
            animation.setDuration(2500);
            view.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    setButtonShadow();
//                    barAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        } else if ( animationType.equals(ANIMATION_RAISE_UP) ) {
            layer.setVisibility(View.VISIBLE);
            TranslateAnimation animation = new TranslateAnimation(0,0, btnHeight, 0);
            animation.setDuration(1000);
            animation.setFillAfter(true);
            view.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    setButtonShadow();
//                    barAnimation();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        } else if ( animationType.equals(ANIMATION_FLICKER) ) {
            Animation animation = new AlphaAnimation(0, 1);
            animation.setDuration(1500);
            animation.setRepeatCount(2);
            layer.setVisibility(View.VISIBLE);
            layer.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    setButtonShadow();
//                    barAnimation();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        } else if ( animationType.equals(ANIMATION_ROTATE) ) {
            int repeatTime = 2;
            Animation animation = new RotateAnimation(0.0f, 360f * repeatTime, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f) ;
            animation.setDuration(1500);
            layer.setVisibility(View.VISIBLE);
            buttonLayer.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    setButtonShadow();
//                    barAnimation();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
//            msgIcon.setVisibility(View.VISIBLE);
//            ObjectAnimator animator = ObjectAnimator.ofFloat(buttonLayer, "rotationY", 0f, 720f);
//            animator.setDuration(2000);
//
//            animator.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {}
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    barAnimation();
//                }
//                @Override
//                public void onAnimationCancel(Animator animation) {}
//                @Override
//                public void onAnimationRepeat(Animator animation) {}
//            });
//            animator.start();
        } else if ( animationType.equals(ANIMATION_SPRING) ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    layer.setVisibility(View.VISIBLE);
                    msgIcon.setVisibility(View.VISIBLE);
                    SpringAnimation animation = new SpringAnimation(view, SpringAnimation.TRANSLATION_Y, 0).setStartVelocity(-8000);
                    animation.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
                    animation.start();
                    animation.addEndListener(new SpringAnimation.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                            setButtonShadow();
//                        barAnimation();
                        }
                    });
                } catch (NoClassDefFoundError e1) {
                    e1.printStackTrace();
                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(2500);
                    layer.setVisibility(View.VISIBLE);
                    layer.setAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            setButtonShadow();
//                        barAnimation();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            } else {
                Animation animation = new AlphaAnimation(0, 1);
                animation.setDuration(2500);
                layer.setVisibility(View.VISIBLE);
                layer.setAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        setButtonShadow();
//                        barAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        } else if ( animationType.equals(ANIMATION_MOVE_LEFT_TO_RIGHT) ) {
            layer.setVisibility(View.VISIBLE);
            msgIcon.setVisibility(View.VISIBLE);
            TranslateAnimation animation = new TranslateAnimation(-(btnWidth / 2),0, 0, 0);
            animation.setDuration(1000);
            animation.setFillAfter(true);
            view.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    setButtonShadow();
//                    barAnimation();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        } else {
            Animation animation = new AlphaAnimation(0, 1);
            animation.setDuration(2500);
            layer.setVisibility(View.VISIBLE);
            msgIcon.setVisibility(View.VISIBLE);
            layer.setAnimation(animation);
            msgIcon.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    setButtonShadow();
//                    barAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
    }

    private String getImageFilePath(boolean isIcon) {
        File root = new File(context.getFilesDir().getAbsolutePath());
        File[] Files = root.listFiles();
        if(Files != null) {
            for(int j = 0; j < Files.length; j++) {
                String filePath = Files[j].getAbsolutePath();
                if ( isIcon ) {
                    if ( filePath.contains(IBotDownloadImage.IMAGE_ICON + apiKey) )
                        return filePath;
                } else {
                    if ( filePath.contains(IBotDownloadImage.IMAGE_CLOSE + apiKey) )
                        return filePath;
                }
            }
        }
        return null;
    }

    private void setGifFile(ImageView view, File file) {
        if ( file != null && view != null ) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    buttonBg.setBackground(null);
                } else {
                    buttonBg.setBackgroundDrawable(null);
                }
                IBotGifAnimationDrawable drawable = new IBotGifAnimationDrawable(file);
                view.setImageDrawable(drawable);
                drawable.setVisible(true, true);
                drawable.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setBackground(ImageView view, Drawable drawable) {
        if ( view != null && drawable != null ) {
            view.setImageDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    }

    private void initImage(ImageView view) {
        if ( view != null ) {
            view.setImageDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(null);
            } else {
                view.setBackgroundDrawable(null);
            }
        }
    }

    private void autoScrollExplain() {
        ViewTreeObserver treeObserver = textExplain.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textExplain.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                getScrollMaxAmount();
                startAutoScrolling();
            }
        });
    }

    public void getScrollMaxAmount(){
        int actualWidth = (textExplain.getMeasuredWidth()-512);
        scrollMax = actualWidth;
    }

    public void startAutoScrolling() {
        try {
            if (timer == null) {
                timer = new Timer();

                if(timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }

                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        moveScrollView();
                    }
                };
                timer.schedule(timerTask, 1000, 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveScrollView() {
        scrollPosition = (int) (root.getScrollX() + 1.0);
        if(scrollPosition >= scrollMax) {
            scrollPosition = 0;
        }
        root.scrollTo(scrollPosition, 0);
    }

    private void setButtonShadow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float cornerRadius = dpToPx(35);
            IBotOutlineProvider outlineProvider = new IBotOutlineProvider(cornerRadius, 0.96f, 1.32f, 0);
            btnBackground.setOutlineProvider(outlineProvider);
            maskLayer.setElevation(17);
//            buttonBg.setElevation(17);
            buttonLayer.setElevation(17);
            msgIcon.setElevation(17);

            // 그림자에 fade in animation 효과 주기 위해
            translationZ = 0;
            try {
                if (eTimer == null) {
                    eTimer = new Timer();

                    if(eTimerTask != null) {
                        eTimerTask.cancel();
                        eTimerTask = null;
                    }

                    eTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            if ( translationZ < 16 ) {
                                translationZ = translationZ + 1;
                                btnBackground.setTranslationZ(translationZ);
                            } else {
                                barAnimation();
                                eTimerTask.cancel();
                                eTimerTask = null;
                            }
                        }
                    };
                    eTimer.schedule(eTimerTask, 0, 30);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.e("TAG", "onDetachedFromWindow");
        if(timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if(eTimerTask != null) {
            eTimerTask.cancel();
            eTimerTask = null;
        }
        super.onDetachedFromWindow();
    }
}