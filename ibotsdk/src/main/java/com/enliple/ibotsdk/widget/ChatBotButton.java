package com.enliple.ibotsdk.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.enliple.ibotsdk.ActivityPresenter;
import com.enliple.ibotsdk.R;

public class ChatBotButton  extends FrameLayout {
    private static final int TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON = 0; // button은 화면 우측에 위치하고 왼쪽으로 expanding area가 노출됨
    private static final int TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON = 1; // button은 화면 좌측에 위치하고 오른쪾으로 expanding area가 노출됨
    private static final int TYPE_NON_EXPANDABLE_BUTTON = 2; // expanding area가 노출되지 않으며 버튼 위치는 어느곳에든 위치할 수 있음
    private static final int DEFAULT_SIZE = 60; // default ChatBotButton size
    private static final int DEFAULT_TEXT_SIZE = 15; // expanding area에 있는 문구의 default size

    private Context context;

    private FrameLayout frame;
    private RelativeLayout layer;
    private RelativeLayout root;
    private RelativeLayout buttonClose;
    private AppCompatImageView buttonCloseImage;
    private AppCompatTextView textExplain;
    private RelativeLayout buttonBg;

    private int type = TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON;
    private int bBgImage;
    private int cBtnImage;
    private int barBg;
    private int barText;
    private int barTextColor;
    private int size = DEFAULT_SIZE;
    private int barTextSize = DEFAULT_TEXT_SIZE;

    public ChatBotButton(Context context) {
        super(context);
        initViews(context);
    }

    public ChatBotButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
        getAttrs(attrs);
    }

    public ChatBotButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
        getAttrs(attrs, defStyleAttr);
    }

    public ChatBotButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews(context);
        getAttrs(attrs, defStyleRes);
    }

    private void initViews(Context context) {
        this.context = context;
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.chatbot_button, this, false);
        addView(v);

        frame = findViewById(R.id.frame);
        layer = findViewById(R.id.layer);
        root = findViewById(R.id.root);
        buttonClose = findViewById(R.id.buttonClose);
        buttonCloseImage = findViewById(R.id.buttonCloseImage);
        textExplain = findViewById(R.id.textExplain);
        buttonBg = findViewById(R.id.buttonBg);

        layer.setOnClickListener(clickListener);
        buttonClose.setOnClickListener(clickListener);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ChatBotButton);
        setTypeArray(typedArray);
    }


    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ChatBotButton, defStyle, 0);
        setTypeArray(typedArray);
    }


    private void setTypeArray(TypedArray typedArray) {
        type = typedArray.getInt(R.styleable.ChatBotButton_type, TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON);
        bBgImage = typedArray.getResourceId(R.styleable.ChatBotButton_buttonBg, R.drawable.ibot_icon);
        cBtnImage = typedArray.getResourceId(R.styleable.ChatBotButton_buttonCloseIcon, R.drawable.ibot_ico_close);
        barBg = typedArray.getColor(R.styleable.ChatBotButton_barBg, ContextCompat.getColor(context, R.color.bar_background));
        barText = typedArray.getResourceId(R.styleable.ChatBotButton_barText, R.string.hello_ibot);
        barTextColor = typedArray.getColor(R.styleable.ChatBotButton_barTextColor, ContextCompat.getColor(context, R.color.ibot_text_color));
//        expandable = typedArray.getBoolean(R.styleable.ChatBotButton_expandable, true);
        size = typedArray.getInt(R.styleable.ChatBotButton_size, DEFAULT_SIZE);
        barTextSize = typedArray.getInt(R.styleable.ChatBotButton_barTextSize, DEFAULT_TEXT_SIZE);
        size = dpToPx(size);
        float radius = size / 2;
        if ( type == TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON ) {
            GradientDrawable buttonBarBackground = new GradientDrawable();
            buttonBarBackground.setShape(GradientDrawable.RECTANGLE);
            buttonBarBackground.setCornerRadii(new float[] {radius, radius, 0f, 0f, 0f, 0f, radius, radius});
            buttonBarBackground.setColor(barBg);

            RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
            buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            buttonParams.width = size;
            buttonParams.height = size;
            buttonBg.setLayoutParams(buttonParams);

            RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
            rootParams.height = size;
            rootParams.setMargins(0, 0, (int)radius, 0);
            rootParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            root.setLayoutParams(rootParams);

            FrameLayout.LayoutParams layerParams = (FrameLayout.LayoutParams)layer.getLayoutParams();
            layerParams.height = size;
            layerParams.gravity = Gravity.RIGHT;
            layer.setLayoutParams(layerParams);

            ViewGroup.LayoutParams closeIconParam = buttonCloseImage.getLayoutParams();
            closeIconParam.width = size / 5;
            closeIconParam.height = size / 5;
            buttonCloseImage.setLayoutParams(closeIconParam);

            RelativeLayout.LayoutParams buttonCloseParams = (RelativeLayout.LayoutParams)buttonClose.getLayoutParams();
            buttonCloseParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            buttonClose.setLayoutParams(buttonCloseParams);

            RelativeLayout.LayoutParams explainParams = (RelativeLayout.LayoutParams)textExplain.getLayoutParams();
            explainParams.addRule(RelativeLayout.CENTER_VERTICAL);
            explainParams.addRule(RelativeLayout.RIGHT_OF, R.id.buttonClose);
            textExplain.setLayoutParams(explainParams);

            root.setBackground(buttonBarBackground);
            buttonBg.setBackgroundResource(bBgImage);
            buttonCloseImage.setBackgroundResource(cBtnImage);

            textExplain.setPadding(0, 0, (int)radius + 20, 0);
            textExplain.setText(barText);
            textExplain.setTextColor(barTextColor);
            textExplain.setTextSize(barTextSize);
        } else if ( type == TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON ) {
            GradientDrawable buttonBarBackground = new GradientDrawable();
            buttonBarBackground.setShape(GradientDrawable.RECTANGLE);
            buttonBarBackground.setCornerRadii(new float[] {0f, 0f, radius, radius, radius, radius, 0f, 0f});
            buttonBarBackground.setColor(barBg);

            RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
            buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            buttonParams.width = size;
            buttonParams.height = size;
            buttonBg.setLayoutParams(buttonParams);

            RelativeLayout.LayoutParams rootParams = (RelativeLayout.LayoutParams)root.getLayoutParams();
            rootParams.height = size;
            rootParams.setMargins((int)radius, 0, 0, 0);
            rootParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            root.setLayoutParams(rootParams);

            FrameLayout.LayoutParams layerParams = (FrameLayout.LayoutParams)layer.getLayoutParams();
            layerParams.height = size;
            layerParams.gravity = Gravity.LEFT;
            layer.setLayoutParams(layerParams);

            ViewGroup.LayoutParams closeIconParam = buttonCloseImage.getLayoutParams();
            closeIconParam.width = size / 5;
            closeIconParam.height = size / 5;
            buttonCloseImage.setLayoutParams(closeIconParam);

            RelativeLayout.LayoutParams buttonCloseParams = (RelativeLayout.LayoutParams)buttonClose.getLayoutParams();
            buttonCloseParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            buttonClose.setLayoutParams(buttonCloseParams);

            RelativeLayout.LayoutParams explainParams = (RelativeLayout.LayoutParams)textExplain.getLayoutParams();
            explainParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            explainParams.addRule(RelativeLayout.LEFT_OF, R.id.buttonClose);
            explainParams.addRule(RelativeLayout.CENTER_VERTICAL);
            textExplain.setLayoutParams(explainParams);

            root.setBackground(buttonBarBackground);
            buttonBg.setBackgroundResource(bBgImage);
            buttonCloseImage.setBackgroundResource(cBtnImage);

            textExplain.setPadding((int)radius + 20, 0, 0, 0);
            textExplain.setText(barText);
            textExplain.setTextColor(barTextColor);
            textExplain.setTextSize(barTextSize);
        } else {
            ViewGroup.LayoutParams frameParams = frame.getLayoutParams();
            frameParams.width = size;
            frameParams.height = size;
            frame.setLayoutParams(frameParams);

            ViewGroup.LayoutParams layerParams = layer.getLayoutParams();
            layerParams.width = size;
            layerParams.height = size;
            layer.setLayoutParams(layerParams);

            root.setVisibility(View.GONE);

            RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)buttonBg.getLayoutParams();
            buttonParams.width = size;
            buttonParams.height = size;
            buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            buttonBg.setLayoutParams(buttonParams);

            buttonBg.setBackgroundResource(bBgImage);
        }

        barAnimation();

        typedArray.recycle();
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
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            // expanding area 다 열리고 나서 text 문구 노출 시킴 (with. Alpha animation)
                            Animation animation = new AlphaAnimation(0, 1);
                            animation.setDuration(2000);
                            textExplain.setVisibility(View.VISIBLE);
                            textExplain.setAnimation(animation);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
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
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                root.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }

    private View.OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if  ( id == R.id.buttonClose ) {
                closeBar();
            } else if ( id == R.id.layer ) {
                ActivityPresenter.shared.presentChatbotActivity(getContext(), "https://bot.istore.camp/index.html?origin=http%3A%2F%2Fm.superbeeracing.com&referer=http%3A%2F%2Fm.superbeeracing.com%2F&mallId=8");
            }
        }
    };

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
