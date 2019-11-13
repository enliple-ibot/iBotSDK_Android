package com.enliple.ibotsdk.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.enliple.ibotsdk.IBotSDK;
import com.enliple.ibotsdk.R;
import com.enliple.ibotsdk.common.IBotAppPreferences;
import com.enliple.ibotsdk.common.IBotDownloadImage;
import com.enliple.ibotsdk.network.IBotNetworkAsyncTask;

import java.io.File;

public class IBotChatButton extends FrameLayout {
    public static final int TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON = 0; // button은 화면 우측에 위치하고 왼쪽으로 expanding area가 노출됨
    public static final int TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON = 1; // button은 화면 좌측에 위치하고 오른쪾으로 expanding area가 노출됨
    public static final int TYPE_NON_EXPANDABLE_BUTTON = 2; // expanding area가 노출되지 않으며 버튼 위치는 어느곳에든 위치할 수 있음
    public static final int DEFAULT_SIZE = 60; // default ChatBotButton size
    public static final int DEFAULT_TEXT_SIZE = 14; // expanding area에 있는 문구의 default size
    public static final int ANIMATION_NONE = -1;
    public static final int ANIMATION_FADE_IN = 0;
    public static final int ANIMATION_RAISE_UP = 1;
    public static final int ANIMATION_FLICKER = 2;
    public static final int ANIMATION_ROTATE = 3;
    public static final int ANIMATION_SPRING = 4;

    private Context context;
    private IBotSDK sdk;
    private ViewGroup view;
    private FrameLayout frame;
    private RelativeLayout layer;
    private RelativeLayout root;
    private RelativeLayout buttonClose;
    private ImageView buttonCloseImage;
    private TextView textExplain;
    private ImageView buttonBg;

    private int type = TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON;
    private int animationType = ANIMATION_NONE;
    private GradientDrawable buttonBarBackground;
    private Drawable bBgImage;
    private Drawable cBtnImage;
    private int barBg;
    private int barTextColor;
    private int size = DEFAULT_SIZE;
    private int barTextSize = DEFAULT_TEXT_SIZE;
    private float radius;
    private String barText;
    private String apiKey = null;

    public IBotChatButton(Context context, String apiKey, int type, int animationType, ViewGroup view, IBotSDK sdk) {
        super(context);
        this.sdk = sdk;
        this.type = type;
        this.view = view;
        this.animationType = animationType;
        setApiKey(apiKey);
        initViews(context);
    }

    public IBotChatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IBotChatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initViews(Context context) {
        this.context = context;
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) context.getSystemService(infService);
        View v = li.inflate(R.layout.ibot_chat_button, this, false);
        addView(v);

        frame = findViewById(R.id.frame);
        layer = findViewById(R.id.layer);
        root = findViewById(R.id.root);
        buttonClose = findViewById(R.id.buttonClose);
        buttonCloseImage = findViewById(R.id.buttonCloseImage);
        textExplain = findViewById(R.id.textExplain);
        buttonBg = findViewById(R.id.buttonBg);

//        layer.setOnClickListener(clickListener);
        buttonClose.setOnClickListener(clickListener);

        bBgImage = context.getResources().getDrawable(R.drawable.ibot_icon);
        cBtnImage = context.getResources().getDrawable(R.drawable.ibot_close_white_ico);
        barBg = context.getResources().getColor(R.color.ibot_bar_background);
        barTextColor = context.getResources().getColor( R.color.ibot_text_color);
        barText = context.getResources().getString(R.string.hello_ibot);
        barTextSize = DEFAULT_TEXT_SIZE;
        size = dpToPx(DEFAULT_SIZE);
        radius = size / 2;

        File iconFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + IBotDownloadImage.IMAGE_ICON);
        File closeFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + IBotDownloadImage.IMAGE_CLOSE);

        if ( iconFile.exists() ) {
            Bitmap iconBitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
            bBgImage = new BitmapDrawable(context.getResources(), iconBitmap);
        }

        if ( closeFile.exists() ) {
            Bitmap closeBitmap = BitmapFactory.decodeFile(closeFile.getAbsolutePath());
            cBtnImage = new BitmapDrawable(context.getResources(), closeBitmap);
        }

        String bgColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_BUTTON_BG_COLOR + "_" + apiKey);
        if ( !TextUtils.isEmpty(bgColor) )
            barBg = Color.parseColor(bgColor);

        String textColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_TEXT_COLOR + "_" + apiKey);
        if ( !TextUtils.isEmpty(textColor) )
            barTextColor = Color.parseColor(textColor);

        String text = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_TEXT + "_" + apiKey);
        if ( !TextUtils.isEmpty(text) )
            barText = text;
        Bitmap bitmapBgImage = ((BitmapDrawable) bBgImage).getBitmap();
        int bBgImageWidth = bitmapBgImage.getWidth();
        int bBgImageHeight = bitmapBgImage.getHeight();
        int bgImageWidth = (bBgImageWidth * DEFAULT_SIZE) / bBgImageHeight;
        bgImageWidth = dpToPx(bgImageWidth);
        Log.e("TAG", "bgImageWidth :: " + bgImageWidth + " , size :: " + size );
        if ( type == TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON ) {
            buttonBarBackground = new GradientDrawable();
            buttonBarBackground.setShape(GradientDrawable.RECTANGLE);
            buttonBarBackground.setCornerRadii(new float[] {radius, radius, 0f, 0f, 0f, 0f, radius, radius});
            buttonBarBackground.setColor(barBg);

            RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
            buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            buttonParams.width = bgImageWidth;
            buttonParams.height = size;
            buttonBg.setLayoutParams(buttonParams);

            RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
            rootParams.height = size;
            rootParams.setMargins(0, 0, (int)(bgImageWidth / 2), 0);
            rootParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            root.setLayoutParams(rootParams);

            LayoutParams layerParams = (LayoutParams)layer.getLayoutParams();
            layerParams.height = size;
            layerParams.gravity = Gravity.RIGHT;
            layer.setLayoutParams(layerParams);

            RelativeLayout.LayoutParams buttonCloseParams = (RelativeLayout.LayoutParams)buttonClose.getLayoutParams();
            buttonCloseParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            buttonClose.setLayoutParams(buttonCloseParams);

            RelativeLayout.LayoutParams explainParams = (RelativeLayout.LayoutParams)textExplain.getLayoutParams();
            explainParams.addRule(RelativeLayout.CENTER_VERTICAL);
            explainParams.addRule(RelativeLayout.RIGHT_OF, R.id.buttonClose);
            textExplain.setLayoutParams(explainParams);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                root.setBackground(buttonBarBackground);
                buttonBg.setBackground(bBgImage);
                buttonCloseImage.setBackground(cBtnImage);
            } else {
                root.setBackgroundDrawable(buttonBarBackground);
                buttonBg.setBackgroundDrawable(bBgImage);
                buttonCloseImage.setBackgroundDrawable(cBtnImage);
            }

            textExplain.setPadding(0, 0, (int)(bgImageWidth / 2) + 20, 0);
            textExplain.setText(barText);
            textExplain.setTextColor(barTextColor);
            textExplain.setTextSize(barTextSize);
        } else if ( type == TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON ) {
            buttonBarBackground = new GradientDrawable();
            buttonBarBackground.setShape(GradientDrawable.RECTANGLE);
            buttonBarBackground.setCornerRadii(new float[] {0f, 0f, radius, radius, radius, radius, 0f, 0f});
            buttonBarBackground.setColor(barBg);

            RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
            buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            buttonParams.width = bgImageWidth;
            buttonParams.height = size;
            buttonBg.setLayoutParams(buttonParams);

            RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
            rootParams.height = size;
            rootParams.setMargins((int)(bgImageWidth / 2), 0, 0, 0);
            rootParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            root.setLayoutParams(rootParams);

            LayoutParams layerParams = (LayoutParams)layer.getLayoutParams();
            layerParams.height = size;
            layerParams.gravity = Gravity.LEFT;
            layer.setLayoutParams(layerParams);

            RelativeLayout.LayoutParams buttonCloseParams = (RelativeLayout.LayoutParams)buttonClose.getLayoutParams();
            buttonCloseParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            buttonClose.setLayoutParams(buttonCloseParams);

            RelativeLayout.LayoutParams explainParams = (RelativeLayout.LayoutParams)textExplain.getLayoutParams();
            explainParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            explainParams.addRule(RelativeLayout.LEFT_OF, R.id.buttonClose);
            explainParams.addRule(RelativeLayout.CENTER_VERTICAL);
            textExplain.setLayoutParams(explainParams);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                root.setBackground(buttonBarBackground);
                buttonBg.setBackground(bBgImage);
                buttonCloseImage.setBackground(cBtnImage);
            } else {
                root.setBackgroundDrawable(buttonBarBackground);
                buttonBg.setBackgroundDrawable(bBgImage);
                buttonCloseImage.setBackgroundDrawable(cBtnImage);
            }

            textExplain.setPadding((int)(bgImageWidth / 2) + 20, 0, 0, 0);
            textExplain.setText(barText);
            textExplain.setTextColor(barTextColor);
            textExplain.setTextSize(barTextSize);
        } else {
            ViewGroup.LayoutParams frameParams = frame.getLayoutParams();
            frameParams.width = bgImageWidth;
            frameParams.height = size;
            frame.setLayoutParams(frameParams);

            ViewGroup.LayoutParams layerParams = layer.getLayoutParams();
            layerParams.width = bgImageWidth;
            layerParams.height = size;
            layer.setLayoutParams(layerParams);

            root.setVisibility(View.GONE);

            RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
            buttonParams.width = bgImageWidth;
            buttonParams.height = size;
            buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            buttonBg.setLayoutParams(buttonParams);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                buttonBg.setBackground(bBgImage);
            else
                buttonBg.setBackgroundDrawable(bBgImage);
        }
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    private void barAnimation() {
        if ( type != TYPE_NON_EXPANDABLE_BUTTON ) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    root.setVisibility(View.VISIBLE);
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
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {}

                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    });
                    mAnimator.start();
                }
            }, 3000);
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
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        mAnimator.start();
    }

    private View.OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if  ( id == R.id.buttonClose )
                closeBar();
            else if ( id == R.id.layer ) {
                if ( sdk != null )
                    sdk.goIBotChat();
            }
        }
    };

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

    public void onReceived() {
        try {
            File iconFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + IBotDownloadImage.IMAGE_ICON + apiKey + IBotDownloadImage.IMAGE_FILE_EXTENSION);
            File closeFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + IBotDownloadImage.IMAGE_CLOSE + apiKey + IBotDownloadImage.IMAGE_FILE_EXTENSION);

            if ( iconFile.exists() ) {
                Bitmap iconBitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
                bBgImage = new BitmapDrawable(context.getResources(), iconBitmap);

                Bitmap bitmapBgImage = ((BitmapDrawable) bBgImage).getBitmap();
                int bBgImageWidth = bitmapBgImage.getWidth();
                int bBgImageHeight = bitmapBgImage.getHeight();
                int bgImageWidth = (bBgImageWidth * DEFAULT_SIZE) / bBgImageHeight;
                bgImageWidth = dpToPx(bgImageWidth);

                buttonBarBackground = new GradientDrawable();
                buttonBarBackground.setShape(GradientDrawable.RECTANGLE);
                Log.e("TAG", "bgImageWidth :: " + bgImageWidth + " , size :: " + size );
                if ( type == TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON ) {
                    RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
                    buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    buttonParams.width = bgImageWidth;
                    buttonParams.height = size;
                    buttonBg.setLayoutParams(buttonParams);

                    RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
                    rootParams.height = size;
                    rootParams.setMargins(0, 0, (int)(bgImageWidth / 2), 0);
                    rootParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    root.setLayoutParams(rootParams);

                    textExplain.setPadding(0, 0, (int)(bgImageWidth / 2) + 20, 0);
                    buttonBarBackground.setCornerRadii(new float[] {radius, radius, 0f, 0f, 0f, 0f, radius, radius});
                } else if ( type == TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON ) {
                    RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
                    buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    buttonParams.width = bgImageWidth;
                    buttonParams.height = size;
                    buttonBg.setLayoutParams(buttonParams);

                    RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
                    rootParams.height = size;
                    rootParams.setMargins((int)(bgImageWidth / 2), 0, 0, 0);
                    rootParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    root.setLayoutParams(rootParams);

                    textExplain.setPadding((int)(bgImageWidth / 2) + 20, 0, 0, 0);
                    buttonBarBackground.setCornerRadii(new float[] {0f, 0f, radius, radius, radius, radius, 0f, 0f});
                } else {
                    ViewGroup.LayoutParams frameParams = frame.getLayoutParams();
                    frameParams.width = bgImageWidth;
                    frameParams.height = size;
                    frame.setLayoutParams(frameParams);

                    ViewGroup.LayoutParams layerParams = layer.getLayoutParams();
                    layerParams.width = bgImageWidth;
                    layerParams.height = size;
                    layer.setLayoutParams(layerParams);

                    root.setVisibility(View.GONE);

                    RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
                    buttonParams.width = bgImageWidth;
                    buttonParams.height = size;
                    buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    buttonBg.setLayoutParams(buttonParams);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    buttonBg.setBackground(bBgImage);
                else
                    buttonBg.setBackgroundDrawable(bBgImage);
            }

            if ( closeFile.exists() ) {
                Bitmap closeBitmap = BitmapFactory.decodeFile(closeFile.getAbsolutePath());
                cBtnImage = new BitmapDrawable(context.getResources(), closeBitmap);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    buttonCloseImage.setBackground(cBtnImage);
                else
                    buttonCloseImage.setBackgroundDrawable(cBtnImage);
            }

            String bgColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_BUTTON_BG_COLOR + "_" + apiKey);
            if ( !TextUtils.isEmpty(bgColor) )
                barBg = Color.parseColor(bgColor);
            buttonBarBackground.setColor(barBg);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                root.setBackground(buttonBarBackground);
            else
                root.setBackgroundDrawable(buttonBarBackground);

            String textColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_TEXT_COLOR + "_" + apiKey);
            if ( !TextUtils.isEmpty(textColor) ) {
                barTextColor = Color.parseColor(textColor);
                textExplain.setTextColor(barTextColor);
            }

            String text = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_TEXT + "_" + apiKey);
            if ( !TextUtils.isEmpty(text) ) {
                barText = text;
                textExplain.setText(barText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ( animationType == ANIMATION_FADE_IN ) {
            Animation animation = new AlphaAnimation(0, 1);
            animation.setDuration(2500);
            layer.setVisibility(View.VISIBLE);
            layer.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    barAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        } else if ( animationType == ANIMATION_RAISE_UP ) {
            layer.setVisibility(View.VISIBLE);
            TranslateAnimation animation = new TranslateAnimation(0,0, size, 0);
            animation.setDuration(1000);
            animation.setFillAfter(true);
            view.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    barAnimation();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        } else if ( animationType == ANIMATION_FLICKER ) {
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
                    barAnimation();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        } else if ( animationType == ANIMATION_ROTATE ) {
            layer.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(buttonBg, "rotationY", 0f, 720f);
            animator.setDuration(2000);
            animator.start();
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}
                @Override
                public void onAnimationEnd(Animator animation) {
                    barAnimation();
                }
                @Override
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
        } else if ( animationType == ANIMATION_SPRING ) {
            layer.setVisibility(View.VISIBLE);
            SpringAnimation animation = new SpringAnimation(view, SpringAnimation.TRANSLATION_Y, 0).setStartVelocity(-8000);
            animation.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
            animation.start();
            animation.addEndListener(new SpringAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    barAnimation();
                }
            });
        } else {
            layer.setVisibility(View.VISIBLE);
            barAnimation();
        }
    }
}