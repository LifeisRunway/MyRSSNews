package com.imra.mynews.di.modules;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Date: 22.05.2020
 * Time: 20:13
 *
 * @author IMRA027
 */

public class RetrofitHtmlModule {

    public static String BASE_URL = "https://habr.com/";

//    @Provides
//    @Singleton
//    public Retrofit provideRetrofit (Retrofit.Builder builder) {
//        return builder.baseUrl(BASE_URL).build();
//    }
//
//    @Provides
//    @Singleton
//    public Retrofit.Builder provideRetrofitBuilder (Converter.Factory converterFactory, OkHttpClient.Builder httpClient) {
//        return new Retrofit.Builder()
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
//                .client(httpClient.build());
//    }
//
//    @Provides
//    @Singleton
//    public OkHttpClient.Builder provideHttpClient() {
//        return new OkHttpClient.Builder()
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS);
//    }

}
