package com.enliple.ibotsdk.shadow;

import android.annotation.SuppressLint;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;

@SuppressLint("NewApi")
public class IBotOutlineProvider extends ViewOutlineProvider {
    private float cornerRadius = 0f;
    private float scaleX = 0f;
    private float scaleY = 0f;
    private int yShift = 0;
    private Rect rect = new Rect();

    @Override
    public void getOutline(View view, Outline outline) {
        view.getBackground().copyBounds(rect);
        scale(scaleX, scaleY);
        rect.offset(0, yShift);
        outline.setRoundRect(rect, cornerRadius);
    }

    public IBotOutlineProvider(float cornerRadius, float scaleX, float scaleY, int yShift) {
        this.cornerRadius = cornerRadius;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.yShift = yShift;
    }

    private void scale(float scaleX, float scaleY) {
        float newWidth = rect.width() + scaleX;
        float newHeight = rect.height() + scaleY;
        float deltaX = (rect.width() - newWidth) / 2;
        float deltaY = (rect.height() - newHeight) / 2;
        rect.set((int) (rect.left + deltaX), (int) (rect.top + deltaY), (int) (rect.right - deltaX), (int) (rect.bottom - deltaY));
    }
}
