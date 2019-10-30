package com.enliple.ibotsdk.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.enliple.ibotsdk.widget.IBotChatButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class IBotDownloadImage {
    public static final String IMAGE_ICON = "ibot_icon";
    public static final String IMAGE_CLOSE = "ibot_close";

    public static Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return getResizedBitmap(myBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap getResizedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) 200) / width;
        float scaleHeight = ((float) 200) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,  matrix, false);

        return resizedBitmap;
    }

    public static boolean SaveBitmapToFile(Context context, String apiKey, Bitmap bitmap, boolean isIcon) {
        String fileName = IMAGE_ICON + apiKey + ".png";
        if ( !isIcon )
            fileName = IMAGE_CLOSE + apiKey + ".png";
        String strPath = context.getFilesDir().getAbsolutePath() + File.separator + fileName;
        File file = new File(strPath);
        OutputStream out = null;
        try {
            file.createNewFile();
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static boolean isImageExist(Context context, String apiKey, boolean isIcon) {
        String fileName = IMAGE_ICON + apiKey + ".png";
        if ( !isIcon )
            fileName = IMAGE_CLOSE + apiKey + ".png";
        String strPath = context.getFilesDir().getAbsolutePath() + File.separator + fileName;
        File file = new File(strPath);
        if ( file != null ) {
            return file.exists();
        } else {
            return false;
        }
    }
}