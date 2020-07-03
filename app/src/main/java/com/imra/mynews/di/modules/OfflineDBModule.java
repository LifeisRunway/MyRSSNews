package com.imra.mynews.di.modules;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.imra.mynews.di.common.ArticleDao;
import com.imra.mynews.di.common.OfflineDB;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Date: 02.07.2020
 * Time: 19:59
 *
 * @author IMRA027
 */

@Module
public class OfflineDBModule {

    @Provides
    @Singleton
    public OfflineDB provideOfflineDB (Context context) {
        return Room.databaseBuilder(context, OfflineDB.class, OfflineDB.DATABASE_NAME).allowMainThreadQueries().build();
    }

    @Provides
    @Singleton
    public ArticleDao provideArticleDAO (OfflineDB offlineDB) {
        return offlineDB.articleDao();
    }

}
