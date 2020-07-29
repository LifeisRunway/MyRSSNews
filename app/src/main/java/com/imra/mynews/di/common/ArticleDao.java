package com.imra.mynews.di.common;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.models.RssFeedArticlesDetail;

import java.util.ArrayList;
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

    @Query("SELECT * FROM articles WHERE isSaved = :isSaved")
    List<Article> getSavedArticles(boolean isSaved);

    @Query("SELECT * FROM rssfeeds")
    List<RSSFeed> getAllRssFeeds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert (Article article);

    @Transaction
    @Query("SELECT * FROM rssfeeds WHERE rssFeedId = :rssFeedId")
    RssFeedArticlesDetail getRssFeedArticleDetail (Integer rssFeedId);
    
    @Transaction
    @Query("SELECT * FROM rssfeeds")
    List<RssFeedArticlesDetail> getRssFeedArticleDetails();

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
    List<Long> insertArticles (List<Article> articles);

    @Transaction
    default void insertOrUpdateRssFeedArticles (RssFeedArticlesDetail rssFeedArticlesDetail) {
        List<Article> temp = rssFeedArticlesDetail.getArticles();
        List<Long> insertResult = insertArticles(temp);
        List<Article> updateList = new ArrayList<Article>();

        for(int i = 0; i < insertResult.size(); i++) {
            if(insertResult.get(i) == -1L) {updateList.add(temp.get(i));}
        }

        if(!updateList.isEmpty()) {
            updateArticles(updateList);
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insertRssFeed (RSSFeed rssFeed);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long saveArticles (Article article);

    @Update
    void updateArticle (Article article);

    @Update
    void updateArticles (List<Article> articles);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insertArticles (Article article);

    @Delete
    void delete (Article article);

    //@Query("SELECT * FROM articles WHERE rssId = :rssId AND title = :title")
    //Article getArticle (Integer rssId, String title);

    @Query("SELECT * FROM articles WHERE title = :title")
    Article getArticle (String title);

    @Query("SELECT * FROM rssfeeds WHERE title = :title")
    RSSFeed getRssFeed (String title);

    //@Query("DELETE FROM articles WHERE rssId = :rssId AND title = :title")
    //void deleteArticle (Integer rssId, String title);

    @Query("DELETE FROM articles WHERE  title = :title")
    void deleteArticle (String title);

    @Query("DELETE FROM rssfeeds WHERE url = :url")
    void deleteRssFeed (String url);
}
