package com.ELayang.Desa.API;

import android.util.Log;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

public class RetroServer {
    //    public static String server = "https://elades.pbltifnganjuk.com";
    public static String server = "http://10.0.2.2/si-kunir-web";
    private static final String finalurl = server + "/DatabaseMobile/";

    public static final String API_IMAGE = server + "/uploads/";
    public static final String API_FotoProfil = server + "/";

    private static Retrofit retro;

    public String getUrlImage() {
        return API_IMAGE;
    }

    public static Retrofit konekRetrofit() {
        if (retro == null) {

            // 🔍 Logging HTTP body
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d("Retrofit-HTTP", message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 🔍 Tambahkan Interceptor Custom untuk Debug Header dan URL
            Interceptor debugInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();

                    long t1 = System.nanoTime();
                    Log.d("Retrofit-Debug", "🔹 Request URL: " + request.url());
                    Log.d("Retrofit-Debug", "🔹 Method: " + request.method());
                    Log.d("Retrofit-Debug", "🔹 Headers: " + request.headers());
                    if (request.body() != null) {
                        Log.d("Retrofit-Debug", "🔹 Request Body: " + request.body().toString());
                    }

                    Response response = chain.proceed(request);

                    long t2 = System.nanoTime();
                    Log.d("Retrofit-Debug", String.format("🔸 Response for %s in %.1fms",
                            response.request().url(), (t2 - t1) / 1e6d));
                    Log.d("Retrofit-Debug", "🔸 Response Code: " + response.code());
                    Log.d("Retrofit-Debug", "🔸 Response Message: " + response.message());

                    return response;
                }
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(debugInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();

            Log.d("Retrofit-Init", "🌐 Base URL Retrofit: " + finalurl);

            retro = new Retrofit.Builder()
                    .baseUrl(finalurl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retro;
    }
}