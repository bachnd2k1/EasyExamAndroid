package com.practice.easyexam.app.data.remote;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.practice.easyexam.BuildConfig;
import com.practice.easyexam.app.model.QuestionResponse;
import com.practice.easyexam.app.model.QuestionResponseDeserializer;
import com.practice.easyexam.app.model.TestResponse;
import com.practice.easyexam.app.model.TestResponseDeserializer;

import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static String IP = "192.168.1.236";
    public static final String PORT = "80";
        public static final String BASE_URL = "http://" + IP + ":" + PORT+ "/Exam/";
//public static final String BASE_URL = "https://84c9-2405-4802-1cb5-f3d0-4c62-2505-c0d1-1173.ngrok-free.app/Exam/";
    private static Retrofit retrofit;
    public static Retrofit getInstance() {
//        Gson gson = new GsonBuilder().setLenient().create();
        Gson gson = new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(TestResponse.class, new TestResponseDeserializer())
//                .registerTypeAdapter(QuestionResponse.class,new QuestionResponseDeserializer())
                .create();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("NetworkLog", message);
            }
        });
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build();
        }
        return retrofit;
    }
}
