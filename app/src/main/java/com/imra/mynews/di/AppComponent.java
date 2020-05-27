package com.imra.mynews.di;

import android.content.Context;

import com.imra.mynews.di.modules.ContextModule;
import com.imra.mynews.di.modules.MyNewsModule;
import com.imra.mynews.mvp.MyNewsService;
import com.imra.mynews.mvp.presenters.RepositoriesPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Date: 27.07.2019
 * Time: 17:45
 *
 * @author IMRA027
 */

@Singleton
@Component (modules = {ContextModule.class, MyNewsModule.class})
public interface AppComponent {

    Context getContext();
    MyNewsService getMyNewsService();

    void inject(RepositoriesPresenter presenter);

}
