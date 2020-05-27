package com.imra.mynews.di.modules;

import com.imra.mynews.app.MyNewsApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Date: 27.07.2019
 * Time: 19:48
 *
 * @author IMRA027
 */

@Module(includes = {RetrofitModule.class})
public class ApiModules {

    @Provides
    @Singleton
    public MyNewsApi provideAuthApi(Retrofit retrofit) {return retrofit.create(MyNewsApi.class);}

}
