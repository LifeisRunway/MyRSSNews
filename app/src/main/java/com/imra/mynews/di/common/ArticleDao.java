package com.imra.mynews.di.common;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.models.RssFeedArticlesDetail;

import java.util.List;

import io.reactivex.Single;

/**
 * Date: 01.07.2020
 * Time: 20:42
 *
 * @author IMRA027
 */

@Dao
public interface ArticleDao {

    @Query("SELECT * FROM articles")
    List<Article> getAll();

    @Query("SELECT * FROM rssfeeds")
    List<RSSFeed> getAllRssFeeds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert (Article article);

    @Transaction
    @Query("SELECT * FROM rssfeeds WHERE rssFeedId = :rssFeedId")
    RssFeedArticlesDetail getRssFeedArticleDetail (Integer rssFeedId);
    
    @Transaction
    @Query("SELECT * FROM rssfeeds")
    List<RssFeedArticlesDetail>getRssFeedArticleDetails();

    @Transaction
    @Query("SELECT * FROM rssfeeds WHERE url = :url")
    RssFeedArticlesDetail getRssFeedArticleDetail2 (String url);

    @Transaction
    default void insertRssFeedArticles(RssFeedArticlesDetail rssFeedArticlesDetail) {
        insertRssFeed(rssFeedArticlesDetail.getRssFeed());
        for(Article article : rssFeedArticlesDetail.getArticles()) {
            insertArticles(article);
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insertRssFeed (RSSFeed rssFeed);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long saveArticles (Article article);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insertArticles (Article article);

    @Delete
    void delete (Article article);

    @Query("SELECT * FROM articles WHERE rssId = :rssId AND title = :title")
    Article getArticle (Integer rssId, String title);

    @Query("SELECT * FROM rssfeeds WHERE title = :title")
    RSSFeed getRssFeed (String title);

    @Query("DELETE FROM articles WHERE rssId = :rssId AND title = :title")
    void deleteArticle (Integer rssId, String title);

}
