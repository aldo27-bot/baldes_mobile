package com.ELayang.Desa.API;

import android.util.Log;
import com.google.gson.Gson; // Import tambahan
import com.google.gson.GsonBuilder; // Import tambahan
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

public class RetroServer {
//    public static String server = "http://10.0.2.2/si-kunir-web";

    public static String server = "https://sikunir.pbltifnganjuk.com";
    private static final String finalurl = server + "/DatabaseMobile/";

    public static final String API_IMAGE = server + "/surat/upload_surat/";
    public static final String API_FotoProfil = server + "/";

    private static Retrofit retro;

    public String getUrlImage() {
        return API_IMAGE;
    }

    public static Retrofit konekRetrofit() {
        if (retro == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d("Retrofit-HTTP", message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

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
                    .addInterceptor(chain -> {
                        Request newReq = chain.request().newBuilder()
                                .header("User-Agent", "AndroidApp/1.0")   // 🔥 WAJIB
                                .header("Accept", "*/*")
                                .build();
                        return chain.proceed(newReq);
                    })
                    .addInterceptor(debugInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();

            Log.d("Retrofit-Init", "🌐 Base URL Retrofit: " + finalurl);

            retro = new Retrofit.Builder()
                    .baseUrl(finalurl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }

        return retro;
    }
}