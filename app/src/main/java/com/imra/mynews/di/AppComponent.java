package com.imra.mynews.di;

import android.content.Context;

import com.imra.mynews.di.common.ArticleDao;
import com.imra.mynews.di.common.MyAppScope;
import com.imra.mynews.di.modules.ContextModule;
import com.imra.mynews.di.modules.MyNewsModule;
import com.imra.mynews.di.modules.OfflineDBModule;
import com.imra.mynews.di.modules.PreferenceModule;
import com.imra.mynews.mvp.MyNewsService;
import com.imra.mynews.mvp.presenters.DrawerPresenter;
import com.imra.mynews.mvp.presenters.MainPresenter;
import com.imra.mynews.mvp.presenters.RepositoriesPresenter;
import com.imra.mynews.mvp.presenters.RepositoryPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Date: 27.07.2019
 * Time: 17:45
 *
 * @author IMRA027
 */

@Singleton
@Component (modules = {ContextModule.class, MyNewsModule.class, OfflineDBModule.class, PreferenceModule.class})
//@MyAppScope
public interface AppComponent {

    Context getContext();
    MyNewsService getMyNewsService();
    ArticleDao getAD();
    //SharedPreferences getSharedPref();
    //SharedPreferences.Editor getSPEditor();
    //Integer mLocalDB();

    void inject(RepositoriesPresenter presenter);
    void inject2(RepositoryPresenter presenter);
    void inject3(DrawerPresenter presenter);
    void inject4(MainPresenter presenter);
}
