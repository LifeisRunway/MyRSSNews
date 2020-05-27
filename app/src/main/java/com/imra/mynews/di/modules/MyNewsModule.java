package com.imra.mynews.di.modules;

import com.imra.mynews.app.MyNewsApi;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.mvp.MyNewsService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Date: 28.07.2019
 * Time: 15:49
 *
 * @author IMRA027
 */

@Module(includes = {ApiModules.class})
public class MyNewsModule {

    @Provides
    @Singleton
    public MyNewsService provideMyNewsService (MyNewsApi authApi) {return new MyNewsService(authApi);}

}
