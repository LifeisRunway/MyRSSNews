package com.imra.mynews.di.modules;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.imra.mynews.di.common.ArticleDao;
import com.imra.mynews.di.common.OfflineDB;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Date: 28.07.2019
 * Time: 15:21
 *
 * @author IMRA027
 */

@Module
public class ContextModule {
    private Context mContext;

    public ContextModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return mContext;
    }

//    @Provides
//    @Singleton
//    public OfflineDB provideOfflineDB (Context context) {
//        return Room.databaseBuilder(context, OfflineDB.class, OfflineDB.DATABASE_NAME).build();
//    }
//
//    @Provides
//    @Singleton
//    public ArticleDao provideArticleDAO (OfflineDB offlineDB) {
//        return offlineDB.articleDao();
//    }


}
