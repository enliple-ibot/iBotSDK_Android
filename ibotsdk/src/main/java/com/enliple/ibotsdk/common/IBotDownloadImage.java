package com.enliple.ibotsdk.common;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class IBotDownloadImage {
//    public static final String IMAGE_FILE_EXTENSION = ".png";
    public static final String IMAGE_FILE_EXTENSION_PNG = ".png";
    public static final String IMAGE_FILE_EXTENSION_GIF = ".gif";
    public static final String IMAGE_ICON = "ibot_icon";
    public static final String IMAGE_CLOSE = "ibot_close";

    public static boolean DownloadImage(Context context, String path, String apiKey, boolean type) {
        if ( path == null )
            return false;
        String savePath = context.getFilesDir().getAbsolutePath();
        File dir = new File(savePath);
        String extension = IBotDownloadImage.IMAGE_FILE_EXTENSION_PNG;
        if ( IsGifFile(path) )
            extension = IBotDownloadImage.IMAGE_FILE_EXTENSION_GIF;
        if (!dir.exists())
            dir.mkdirs();
        String fileName = IBotDownloadImage.IMAGE_ICON + apiKey + extension;
        if ( !type )
            fileName = IBotDownloadImage.IMAGE_CLOSE + apiKey + extension;
        String localPath = savePath + File.separator + fileName;

        try {
            HttpURLConnection urlConnection = (HttpURLConnection)new URL(path).openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            byte[] tByte = new byte[1024];
            File file = new File(localPath);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read;
            for (;;) {
                read = inputStream.read(tByte);
                if (read <= 0)
                    break;
                outputStream.write(tByte, 0, read);
            }
            inputStream.close();
            outputStream.close();
            urlConnection.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    public static boolean IsPngFile(String path) {
//        if ( path != null ) {
//            if ( path.toLowerCase().endsWith(".png") )
//                return true;
//            else
//                return false;
//        } else {
//            return true;
//        }
//    }
public static boolean IsGifFile(String path) {
    if ( path != null ) {
        if ( path.toLowerCase().endsWith(".gif") )
            return true;
        else
            return false;
    } else {
        return true;
    }
}
}
