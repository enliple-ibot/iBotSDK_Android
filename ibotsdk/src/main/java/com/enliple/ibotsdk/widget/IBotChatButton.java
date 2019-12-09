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
import com.enliple.ibotsdk.gif.IBotGifAnimationDrawable;
import com.enliple.ibotsdk.network.IBotNetworkAsyncTask;

import java.io.File;
import java.io.IOException;

public class IBotChatButton extends FrameLayout {
    public static final int TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON = 0; // button은 화면 우측에 위치하고 왼쪽으로 expanding area가 노출됨
    public static final int TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON = 1; // button은 화면 좌측에 위치하고 오른쪾으로 expanding area가 노출됨
    public static final int TYPE_NON_EXPANDABLE_BUTTON = 2; // expanding area가 노출되지 않으며 버튼 위치는 어느곳에든 위치할 수 있음
    public static final int DEFAULT_SIZE = 60; // default ChatBotButton size
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
    private RelativeLayout root;
    private RelativeLayout buttonClose;
    private ImageView buttonCloseImage;
    private TextView textExplain;
    private ImageView buttonBg;

    private int type = TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON;
    private String animationType = ANIMATION_FADE_IN;
    private GradientDrawable buttonBarBackground;
    private Drawable bBgImage;
    private Drawable cBtnImage;
    private int barBg;
    private int barTextColor;
    private int sizeHeight = DEFAULT_SIZE;
    private int sizeWidth = DEFAULT_SIZE;
    private int barTextSize = DEFAULT_TEXT_SIZE;
    private float radius;
    private String barText;
    private String apiKey = null;
    public IBotChatButton(Context context, String apiKey, int type, ViewGroup view, IBotSDK sdk) {
        super(context);
        this.context = context;
        this.type = type;
        this.view = view;
        this.sdk = sdk;

        setApiKey(apiKey);
        initViews();
    }

    public IBotChatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IBotChatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initViews() {

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

        initImage(buttonBg);

        buttonClose.setOnClickListener(clickListener);

        bBgImage = context.getResources().getDrawable(R.drawable.ibot_icon);
        cBtnImage = context.getResources().getDrawable(R.drawable.ibot_close_white_ico);
        barBg = context.getResources().getColor(R.color.ibot_bar_background);
        barTextColor = context.getResources().getColor( R.color.ibot_text_color);
        barText = context.getResources().getString(R.string.hello_ibot);
        barTextSize = DEFAULT_TEXT_SIZE;
        sizeHeight = dpToPx(DEFAULT_SIZE);
        radius = sizeHeight / 2;

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
        }

        if ( closeFile != null && closeFile.exists() ) {
            Bitmap closeBitmap = BitmapFactory.decodeFile(closeFile.getAbsolutePath());
            cBtnImage = new BitmapDrawable(context.getResources(), closeBitmap);
        }

        String bgColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_BAR_BG_COLOR + "_" + apiKey);
        if ( !TextUtils.isEmpty(bgColor) )
            barBg = Color.parseColor(bgColor);

        String textColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_TEXT_COLOR + "_" + apiKey);
        if ( !TextUtils.isEmpty(textColor) )
            barTextColor = Color.parseColor(textColor);

        String text = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_TEXT + "_" + apiKey);
        if ( !TextUtils.isEmpty(text) )
            barText = text;
        String animType = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_ANIMATION_TYPE + "_" + apiKey);
        if ( !TextUtils.isEmpty(animType) )
            animationType = animType;

        Bitmap bitmapBgImage = ((BitmapDrawable) bBgImage).getBitmap();
        int bBgImageWidth = bitmapBgImage.getWidth();
        int bBgImageHeight = bitmapBgImage.getHeight();
        sizeWidth = (bBgImageWidth * DEFAULT_SIZE) / bBgImageHeight;
        sizeWidth = dpToPx(sizeWidth);

        if ( type == TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON ) {
            buttonBarBackground = new GradientDrawable();
            buttonBarBackground.setShape(GradientDrawable.RECTANGLE);
            buttonBarBackground.setCornerRadii(new float[] {radius, radius, 0f, 0f, 0f, 0f, radius, radius});
            buttonBarBackground.setColor(barBg);

            RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
            buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            buttonParams.width = sizeWidth;
            buttonParams.height = sizeHeight;
            buttonBg.setLayoutParams(buttonParams);

            RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
            rootParams.height = sizeHeight;
            rootParams.setMargins(0, 0, (int)(sizeWidth / 2), 0);
            rootParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            root.setLayoutParams(rootParams);

            LayoutParams layerParams = (LayoutParams)layer.getLayoutParams();
            layerParams.height = sizeHeight;
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
                if ( !IBotDownloadImage.IsGifFile(closePath) ) {
                    if ( cBtnImage != null )
                        buttonCloseImage.setBackground(cBtnImage);
                } else {
                    setGifFile(buttonCloseImage, closeFile);
                }
            } else {
                root.setBackgroundDrawable(buttonBarBackground);
                if ( !IBotDownloadImage.IsGifFile(closePath) ) {
                    if ( cBtnImage != null )
                        buttonCloseImage.setBackgroundDrawable(cBtnImage);
                } else {
                    setGifFile(buttonCloseImage, closeFile);
                }
            }

            if ( !IBotDownloadImage.IsGifFile(iconPath) ) {
                setBackground(buttonBg, bBgImage);
            } else {
                setGifFile(buttonBg, iconFile);
            }


            textExplain.setPadding(0, 0, (int)(sizeWidth / 2) + 20, 0);
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
            buttonParams.width = sizeWidth;
            buttonParams.height = sizeHeight;
            buttonBg.setLayoutParams(buttonParams);

            RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
            rootParams.height = sizeHeight;
            rootParams.setMargins((int)(sizeWidth / 2), 0, 0, 0);
            rootParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            root.setLayoutParams(rootParams);

            LayoutParams layerParams = (LayoutParams)layer.getLayoutParams();
            layerParams.height = sizeHeight;
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
                if ( !IBotDownloadImage.IsGifFile(closePath) ) {
                    if ( cBtnImage != null ) {
                        buttonCloseImage.setBackground(cBtnImage);
                    }
                } else {
                    setGifFile(buttonCloseImage, closeFile);
                }
            } else {
                root.setBackgroundDrawable(buttonBarBackground);
                if ( !IBotDownloadImage.IsGifFile(closePath) ) {
                    if ( cBtnImage != null )
                        buttonCloseImage.setBackgroundDrawable(cBtnImage);
                } else {
                    setGifFile(buttonCloseImage, closeFile);
                }
            }

            if ( !IBotDownloadImage.IsGifFile(iconPath) ) {
                setBackground(buttonBg, bBgImage);
            } else {
                setGifFile(buttonBg, iconFile);
            }

            textExplain.setPadding((int)(sizeWidth / 2) + 20, 0, 0, 0);
            textExplain.setText(barText);
            textExplain.setTextColor(barTextColor);
            textExplain.setTextSize(barTextSize);
        } else {
            ViewGroup.LayoutParams frameParams = frame.getLayoutParams();
            frameParams.width = sizeWidth;
            frameParams.height = sizeHeight;
            frame.setLayoutParams(frameParams);

            ViewGroup.LayoutParams layerParams = layer.getLayoutParams();
            layerParams.width = sizeWidth;
            layerParams.height = sizeHeight;
            layer.setLayoutParams(layerParams);

            root.setVisibility(View.GONE);

            RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
            buttonParams.width = sizeWidth;
            buttonParams.height = sizeHeight;
            buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT);
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
                LayoutParams layerParams = (LayoutParams)layer.getLayoutParams();
                layerParams.width = sizeWidth;
                layerParams.height = sizeHeight;
//                layerParams.gravity = Gravity.RIGHT;
                layer.setLayoutParams(layerParams);
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
            if  ( id == R.id.buttonClose ) {
                closeBar();
            }
            else if ( id == R.id.buttonBg ) {

            } else if ( id == R.id.layer) {

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
            String iconFilePath = getImageFilePath(true);
            String closeFilePath = getImageFilePath(false);

            if ( !TextUtils.isEmpty(iconFilePath) ) {
                File iconFile = new File(iconFilePath);
                if ( iconFile.exists() ) {
                    Bitmap iconBitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
                    bBgImage = new BitmapDrawable(context.getResources(), iconBitmap);

                    Bitmap bitmapBgImage = ((BitmapDrawable) bBgImage).getBitmap();
                    int bBgImageWidth = bitmapBgImage.getWidth();
                    int bBgImageHeight = bitmapBgImage.getHeight();
                    sizeWidth = (bBgImageWidth * DEFAULT_SIZE) / bBgImageHeight;
                    sizeWidth = dpToPx(sizeWidth);

                    buttonBarBackground = new GradientDrawable();
                    buttonBarBackground.setShape(GradientDrawable.RECTANGLE);

                    if ( type == TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON ) {
                        RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
                        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        buttonParams.width = sizeWidth;
                        buttonParams.height = sizeHeight;
                        buttonBg.setLayoutParams(buttonParams);

                        RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
                        rootParams.height = sizeHeight;
                        rootParams.setMargins(0, 0, (int)(sizeWidth / 2), 0);
                        rootParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        root.setLayoutParams(rootParams);

                        textExplain.setPadding(0, 0, (int)(sizeWidth / 2) + 20, 0);
                        buttonBarBackground.setCornerRadii(new float[] {radius, radius, 0f, 0f, 0f, 0f, radius, radius});
                    } else if ( type == TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON ) {
                        RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
                        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        buttonParams.width = sizeWidth;
                        buttonParams.height = sizeHeight;
                        buttonBg.setLayoutParams(buttonParams);

                        RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
                        rootParams.height = sizeHeight;
                        rootParams.setMargins((int)(sizeWidth / 2), 0, 0, 0);
                        rootParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        root.setLayoutParams(rootParams);

                        textExplain.setPadding((int)(sizeWidth / 2) + 20, 0, 0, 0);
                        buttonBarBackground.setCornerRadii(new float[] {0f, 0f, radius, radius, radius, radius, 0f, 0f});
                    } else {
                        ViewGroup.LayoutParams frameParams = frame.getLayoutParams();
                        frameParams.width = sizeWidth;
                        frameParams.height = sizeHeight;
                        frame.setLayoutParams(frameParams);

                        ViewGroup.LayoutParams layerParams = layer.getLayoutParams();
                        layerParams.width = sizeWidth;
                        layerParams.height = sizeHeight;
                        layer.setLayoutParams(layerParams);

                        root.setVisibility(View.GONE);

                        RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
                        buttonParams.width = sizeWidth;
                        buttonParams.height = sizeHeight;
                        buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        buttonBg.setLayoutParams(buttonParams);
                    }

                    if ( !IBotDownloadImage.IsGifFile(iconFilePath) ) {
                        setBackground(buttonBg, bBgImage);
                    } else {
                        setGifFile(buttonBg, iconFile);
                    }
                }
            }

            if ( !TextUtils.isEmpty(closeFilePath) ) {
                File closeFile = new File(closeFilePath);
                if ( closeFile.exists() ) {
                    Bitmap closeBitmap = BitmapFactory.decodeFile(closeFile.getAbsolutePath());
                    cBtnImage = new BitmapDrawable(context.getResources(), closeBitmap);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        buttonCloseImage.setBackground(cBtnImage);
                    else
                        buttonCloseImage.setBackgroundDrawable(cBtnImage);
                }
            }

            String bgColor = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_BAR_BG_COLOR + "_" + apiKey);
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

            String animType = IBotAppPreferences.getString(context, IBotAppPreferences.IBOT_ANIMATION_TYPE + "_" + apiKey);
            if ( !TextUtils.isEmpty(animType) ) {
                animationType = animType;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ( animationType.equals(ANIMATION_FADE_IN) ) {
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
        } else if ( animationType.equals(ANIMATION_RAISE_UP) ) {
            layer.setVisibility(View.VISIBLE);
            TranslateAnimation animation = new TranslateAnimation(0,0, sizeHeight, 0);
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
                    barAnimation();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        } else if ( animationType.equals(ANIMATION_ROTATE) ) {
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
        } else if ( animationType.equals(ANIMATION_SPRING) ) {
            try {
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
                        barAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        } else if ( animationType.equals(ANIMATION_MOVE_LEFT_TO_RIGHT) ) {
            layer.setVisibility(View.VISIBLE);
            TranslateAnimation animation = new TranslateAnimation(-(sizeWidth / 2),0, 0, 0);
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
                    barAnimation();
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
}