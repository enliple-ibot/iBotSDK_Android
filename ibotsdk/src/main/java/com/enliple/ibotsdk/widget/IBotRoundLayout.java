package com.enliple.ibotsdk.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.enliple.ibotsdk.R;

/**
 * by https://github.com/christophesmet/android_maskable_layout
 */

public class IBotRoundLayout extends FrameLayout {
    private Handler mHandler;
    private Drawable maskDrawable = null;
    private Bitmap mFinalMask = null;
    private Paint mPaint = null;
    private PorterDuffXfermode mPorterDuffXferMode = null;

    public IBotRoundLayout(Context context) {
        super(context);
    }

    public IBotRoundLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if ( !IBotChatButtonTypeA.useOriginImage )
            construct(context);
    }

    public IBotRoundLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if ( !IBotChatButtonTypeA.useOriginImage )
            construct(context);
    }

    private void construct(Context context) {
        mHandler = new Handler();
        setDrawingCacheEnabled(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null); //Only works for software layers
        mPaint = createPaint(false);
        Resources.Theme theme = context.getTheme();
        if (theme != null) {
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = getContext().getResources().getDrawable(R.drawable.masking_image, context.getTheme());
            } else {
                drawable = getContext().getResources().getDrawable(R.drawable.masking_image);
            }
            initMask(drawable);
            mPorterDuffXferMode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
            initMask(maskDrawable);
        }
        registerMeasure();
    }

    private Paint createPaint(boolean antiAliasing) {
        Paint output = new Paint(Paint.ANTI_ALIAS_FLAG);
        output.setAntiAlias(antiAliasing);
        output.setXfermode(mPorterDuffXferMode);
        return output;
    }

    private void initMask(Drawable input) {
        if (input != null) {
            maskDrawable = input;
            if (maskDrawable instanceof AnimationDrawable) {
                maskDrawable.setCallback(this);
            }
        }
    }

    private Bitmap makeBitmapMask(Drawable drawable) {
        if (drawable != null) {
            if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
                Bitmap mask = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(mask);
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                drawable.draw(canvas);
                return mask;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if ( !IBotChatButtonTypeA.useOriginImage )
            setSize(w, h);
    }

    private void setSize(int width, int height) {
        if (width > 0 && height > 0) {
            if (maskDrawable != null) {
                //Remake the 9patch
                swapBitmapMask(makeBitmapMask(maskDrawable));
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if ( !IBotChatButtonTypeA.useOriginImage ) {
            if (mFinalMask != null && mPaint != null) {
                mPaint.setXfermode(mPorterDuffXferMode);
                canvas.drawBitmap(mFinalMask, 0.0f, 0.0f, mPaint);
                mPaint.setXfermode(null);
            }
        }
    }

    private void registerMeasure() {
        final ViewTreeObserver viewTreeObserver = IBotRoundLayout.this.getViewTreeObserver();
        if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver aliveObserver = viewTreeObserver;
                    if (!aliveObserver.isAlive()) {
                        aliveObserver = IBotRoundLayout.this.getViewTreeObserver();
                    }
                    if (aliveObserver != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            aliveObserver.removeOnGlobalLayoutListener(this);
                        } else {
                            aliveObserver.removeGlobalOnLayoutListener(this);
                        }
                    }
                    swapBitmapMask(makeBitmapMask(maskDrawable));
                }
            });
        }
    }

    @Override
    public void invalidateDrawable(Drawable dr) {
        if ( !IBotChatButtonTypeA.useOriginImage ) {
            if (dr != null) {
                initMask(dr);
                swapBitmapMask(makeBitmapMask(dr));
                invalidate();
            }
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if ( !IBotChatButtonTypeA.useOriginImage ) {
            if (who != null && what != null) {
                mHandler.postAtTime(what, when);
            }
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if ( !IBotChatButtonTypeA.useOriginImage ) {
            if (who != null && what != null) {
                mHandler.removeCallbacks(what);
            }
        }
    }

    private void swapBitmapMask(Bitmap newMask) {
        if (newMask != null) {
            if (mFinalMask != null && !mFinalMask.isRecycled()) {
                mFinalMask.recycle();
            }
            mFinalMask = newMask;
        }
    }
}