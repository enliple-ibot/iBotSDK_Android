package com.enliple.ibotsdk.network;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class IBotNetworkApi {

    public String url;
    public Map<String, String> param;
    public String strJson;
    public CallbackListener callbackListener = null;
    public void getExecute(CallbackListener callbackListener) {
        try {
            this.callbackListener = callbackListener;
            URL ul = makeGetUrl();
            if ( ul == null )
                return;
            new GetTask().execute(ul);
        } catch (Exception e) {
            System.err.println(e.toString());
            if ( this.callbackListener != null )
                this.callbackListener.onResponse(false, "Network Error");
        }
    }

    public void postExecute(CallbackListener callbackListener) {
        try {
            this.callbackListener = callbackListener;
            URL ul = new URL(url);
            if ( ul == null )
                return;
            new PostTask().execute(ul);
        } catch (Exception e) {
            e.printStackTrace();
            if ( this.callbackListener != null )
                this.callbackListener.onResponse(false, "Network Error");
        }
    }

    public URL makeGetUrl() {
        if ( param != null && param.size() > 0 ) {
            Uri.Builder builder = Uri.parse(url).buildUpon();
            for ( String key : param.keySet() )
                builder.appendQueryParameter(key, param.get(key));
            Uri builtUri = builder.build();

            URL lUrl = null;
            try {
                lUrl = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return lUrl;
        } else {
            return null;
        }
    }

    class GetTask extends AsyncTask<URL, Void, Void> {
        @Override
        protected Void doInBackground(URL... urls) {
            try {
                HttpURLConnection con = (HttpURLConnection) urls[0].openConnection();
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setRequestMethod("GET");
                con.setDoOutput(false);

                StringBuilder sb = new StringBuilder();
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null)
                        sb.append(line);
                    br.close();
                    System.out.println(sb.toString());
                    if ( callbackListener != null )
                        callbackListener.onResponse(true, sb.toString());
                } else {
                    System.out.println(con.getResponseMessage());
                    if ( callbackListener != null )
                        callbackListener.onResponse(false, con.getResponseMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if ( callbackListener != null )
                    callbackListener.onResponse(false, "Network Exception");
            }
            return null;
        }
    }

    class PostTask extends AsyncTask<URL, Void, Void> {
        @Override
        protected Void doInBackground(URL... urls) {
            try{
                HttpURLConnection con = (HttpURLConnection) urls[0].openConnection();
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setUseCaches(false);
                con.setDoOutput(true);

                OutputStream os = con.getOutputStream();
                os.write(strJson.getBytes("UTF-8"));
                os.flush();
                os.close();

                StringBuilder sb = new StringBuilder();
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    String line;
                    while ((line = br.readLine()) != null)
                        sb.append(line);
                    System.out.println(sb.toString());
                    if ( callbackListener != null )
                        callbackListener.onResponse(true, sb.toString());
                } else {
                    System.out.println(con.getResponseMessage());
                    if ( callbackListener != null )
                        callbackListener.onResponse(false, con.getResponseMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if ( callbackListener != null )
                    callbackListener.onResponse(false, "Network Exception");
            }
            return null;
        }
    }

    public interface CallbackListener {
        public void onResponse(boolean result, Object obj);
    }
}
