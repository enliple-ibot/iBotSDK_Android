package com.enliple.ibotsdk.model;

import com.enliple.ibotsdk.widget.IBotChatButton;

public class IBotButtonAttribute {
    public int type = IBotChatButton.TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON;
    private int bBgImage = -1;
    private int bCloseImage = -1;
    private int barBgColor = -1;
    private int barText = -1;
    private int barTextColor = -1;
    private int barTextSize = IBotChatButton.DEFAULT_TEXT_SIZE;
    private int size = IBotChatButton.DEFAULT_SIZE;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setButtonBg(int resourceId) {
        this.bBgImage = resourceId;
    }

    public int getButtonBg() {
        return this.bBgImage;
    }

    public void setCloseImage(int resourceId) {
        this.bCloseImage = resourceId;
    }

    public int getCloseImage() {
        return this.bCloseImage;
    }

    public void setBarBgColor(int resourceId) {
        this.barBgColor = resourceId;
    }

    public int getBarBgColor() {
        return this.barBgColor;
    }

    public void setBarText(int resourceId) {
        this.barText = resourceId;
    }

    public int getBarText() {
        return this.barText;
    }

    public void setBarTextColor(int resourceId) {
        this.barTextColor = resourceId;
    }

    public int getBarTextColor() {
        return this.barTextColor;
    }

    public void setBarTextSize(int barTextSize) {
        this.barTextSize = barTextSize;
    }

    public int getBarTextSize() {
        return this.barTextSize;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }
}
