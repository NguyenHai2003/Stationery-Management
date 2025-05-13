package com.example.office_management.retrofit2;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class BaseURL {
    //private static final String BASE_URL = "http://192.168.1.33:8080/api/";
    //private static final String BASE_URL = "http://192.168.8.104:8080/api/";
    //private static final String BASE_URL = "http://172.21.48.73:8080/api/";

    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    private static Retrofit retrofit;

    public static Retrofit getUrl(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context))
                    .build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
