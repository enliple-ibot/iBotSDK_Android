/**
 * Copyright (C) 2013 Orthogonal Labs, Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.enliple.ibotsdk.gif;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Creates an AnimationDrawable from a GIF image.</p>
 *
 * @author Femi Omojola <femi@hipmob.com>
 */
public class IBotGifAnimationDrawable extends AnimationDrawable {
    private boolean decoded;
    private IBotGifDecoder mGifDecoder;
    private Bitmap mTmpBitmap;
    private int height, width;

    public IBotGifAnimationDrawable(File f) throws IOException {
        this(f, false);
    }

    public IBotGifAnimationDrawable(InputStream is) throws IOException {
        this(is, false);
    }

    public IBotGifAnimationDrawable(File f, boolean inline) throws IOException {
        this(new BufferedInputStream(new FileInputStream(f), 32768), inline);
    }

    public IBotGifAnimationDrawable(InputStream is, boolean inline) throws IOException {
        super();
        InputStream bis = is;
        if (!BufferedInputStream.class.isInstance(bis)) bis = new BufferedInputStream(is, 32768);
        decoded = false;
        mGifDecoder = new IBotGifDecoder();
        mGifDecoder.read(bis);
        mTmpBitmap = mGifDecoder.getFrame(0);
        height = mTmpBitmap.getHeight();
        width = mTmpBitmap.getWidth();
        addFrame(new BitmapDrawable(mTmpBitmap), mGifDecoder.getDelay(0));
        setOneShot(mGifDecoder.getLoopCount() != 0);
        setVisible(true, true);
        if (inline)
            loader.run();
        else
            new Thread(loader).start();
    }

    public boolean isDecoded() {
        return decoded;
    }

    private Runnable loader = new Runnable() {
        public void run() {
            mGifDecoder.complete();
            int i, n = mGifDecoder.getFrameCount(), t;
            for (i = 1; i < n; i++) {
                mTmpBitmap = mGifDecoder.getFrame(i);
                t = mGifDecoder.getDelay(i);
                addFrame(new BitmapDrawable(mTmpBitmap), t);
            }
            decoded = true;
            mGifDecoder = null;
        }
    };

    public int getMinimumHeight() {
        return height;
    }

    public int getMinimumWidth() {
        return width;
    }

    public int getIntrinsicHeight() {
        return height;
    }

    public int getIntrinsicWidth() {
        return width;
    }
}
