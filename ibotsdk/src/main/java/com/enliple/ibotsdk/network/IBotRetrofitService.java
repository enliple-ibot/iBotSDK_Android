package com.enliple.ibotsdk.network;

import android.content.Context;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;

public class IBotRetrofitService extends IBotBaseRetrofit {

    public static IBotAPI api(Context context) {
        return (IBotAPI) retrofit( context, IBotAPI.class);
    }

    public interface IBotAPI {

        @GET
        Call<ResponseBody> IBotGetAPI(@retrofit2.http.Url String arg, @Header("Cookie") String cookie);

        @GET
        Call<ResponseBody> IBotGetAPI(@retrofit2.http.Url String arg, @QueryMap Map<String, String> params, @Header("Cookie") String cookie);

        @GET
        Call<ResponseBody> IBotGetAPI(@retrofit2.http.Url String arg, @QueryMap Map<String, String> params);

        @GET
        Call<ResponseBody> IBotGetAPI(@retrofit2.http.Url String arg);

        @POST
        Call<ResponseBody> IBotPostAPI(@retrofit2.http.Url String arg);

        @POST
        Call<ResponseBody> IBotPostAPI(@retrofit2.http.Url String arg, @Header("Cookie") String cookie);

        @FormUrlEncoded
        @POST
        Call<ResponseBody> IBotPostAPI(@retrofit2.http.Url String arg, @FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST
        Call<ResponseBody> IBotPostAPI(@retrofit2.http.Url String arg, @FieldMap Map<String, String> params, @Header("Cookie") String cookie);

        @PUT
        Call<ResponseBody> IBotPutAPI(@retrofit2.http.Url String arg);

        @PUT
        Call<ResponseBody> IBotPutAPI(@retrofit2.http.Url String arg, @FieldMap Map<String, String> param);

        @PUT
        Call<ResponseBody> IBotPutAPI1(@retrofit2.http.Url String arg, @Body RequestBody body);

        @FormUrlEncoded
        @PUT
        Call<ResponseBody> IBotPutAPI(@retrofit2.http.Url String arg, @FieldMap Map<String, String> params, @Header("Cookie") String cookie);

        @DELETE
        Call<ResponseBody> IBotDeleteAPI(@retrofit2.http.Url String arg);

        @DELETE
        Call<ResponseBody> IBotDeleteAPI(@retrofit2.http.Url String arg, @QueryMap Map<String, String> param);

        @DELETE
        Call<ResponseBody> IBotDeleteAPI1(@retrofit2.http.Url String arg, @Body RequestBody body);

        @FormUrlEncoded
        @DELETE
        Call<ResponseBody> IBotDeleteAPI(@retrofit2.http.Url String arg, @QueryMap Map<String, String> params, @Header("Cookie") String cookie);

        @POST
        Call<ResponseBody> IBotPostAPI1(@retrofit2.http.Url String arg, @Body RequestBody body);
    }
}
