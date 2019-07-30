package com.imra.mynews.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Date: 27.07.2019
 * Time: 17:48
 *
 * @author IMRA027
 */

@Module
public class RetrofitModule {

    @Provides
    @Singleton
    public Retrofit provideRetrofit (Retrofit.Builder builder) {
        return builder.baseUrl("https://lenta.ru/").build();
    }

    @Provides
    @Singleton
    public Retrofit.Builder provideRetrofitBuilder (Converter.Factory converterFactory) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(converterFactory);
    }

    @Provides
    @Singleton
    public Converter.Factory provideConverterFactory() {
        return SimpleXmlConverterFactory.create();
    }

//    @Provides
//    @Singleton
//    Persister providePersister () {
//        return new Persister(new AnnotationStrategy());
//    }

}
