package com.imra.mynews.di.common;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;

/**
 * Date: 01.07.2020
 * Time: 20:52
 *
 * @author IMRA027
 */

@Database(entities = {RSSFeed.class, Article.class}, version = 1, exportSchema = false)
public abstract class OfflineDB extends RoomDatabase{

    public abstract ArticleDao articleDao();

    public static final String DATABASE_NAME = "newsoffline.db";
    public static final String LOCAL_DB_NAME = "mLocalDB";

}
