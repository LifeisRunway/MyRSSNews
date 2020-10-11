package com.imra.mynews.di.modules;

import android.content.Context;

import com.imra.mynews.di.common.XmlOrJsonConverterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


/**
 * Date: 27.07.2019
 * Time: 17:48
 *
 * @author IMRA027
 */

@Module
public class RetrofitModule {

    public static String BASE_URL = "https://habr.com/";

    @Provides
    @Singleton
    public Retrofit provideRetrofit (Retrofit.Builder builder) {
        return builder.baseUrl(BASE_URL).build();
    }

    @Provides
    @Singleton
    public Retrofit.Builder provideRetrofitBuilder (Converter.Factory converterFactory, OkHttpClient.Builder httpClient) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(converterFactory)
                .client(httpClient.build());
    }

    @Provides
    @Singleton
    public Converter.Factory provideConverterFactory() {
        return new XmlOrJsonConverterFactory();
    }


    @Provides
    @Singleton
    public OkHttpClient.Builder provideHttpClient(Cache cache) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        httpClient
                //.addInterceptor(logging)
                .cache(cache);
        return httpClient;
    }


//    @Provides
//    @Singleton
//    public HttpLoggingInterceptor provideHttpLoggingInterceptor () {
//        return new HttpLoggingInterceptor()
//                .setLevel(HttpLoggingInterceptor.Level.BODY);
//    }

    @Provides
    @Singleton
    Cache provideCache(Context context){
        int cacheSize = 1024*1024*10;
        return new Cache(context.getCacheDir(),cacheSize);
    }

}
