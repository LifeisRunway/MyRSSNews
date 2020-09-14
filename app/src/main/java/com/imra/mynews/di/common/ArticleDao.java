package com.imra.mynews.di.common;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.ArticleDetail;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.models.RssFeedArticlesDetail;

import java.util.ArrayList;
import java.util.List;

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertArticles (List<Article> articles);

    @Transaction
    default void insertOrUpdateRss (RSSFeed rssFeed) {
        Long temp = insertRssFeed(rssFeed);
        if (temp == -1L) updateRss(rssFeed);
    }

    @Transaction
    default void insertOrUpdateRssFeedArticles (RssFeedArticlesDetail rssFeedArticlesDetail) {
        insertOrUpdateRss(rssFeedArticlesDetail.getRssFeed());
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

    @Update
    void updateRss(RSSFeed rssFeed);

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

    @Query("SELECT * FROM articles WHERE title = :title")
    Article getArticle (String title);

    @Query("SELECT * FROM rssfeeds WHERE url = :url")
    RSSFeed getRssFeed (String url);

    @Query("DELETE FROM articles WHERE  title = :title")
    void deleteArticle (String title);

    @Query("DELETE FROM rssfeeds WHERE url = :url")
    void deleteRssFeed (String url);

    @Transaction
    default void deleteManyRssFeeds (List<String> urls) {
        for(String url : urls) {
            deleteRssFeed(url);
        }
    }

    @Query("SELECT * FROM rssfeeds WHERE url = :url")
    RSSFeed getRssForDrawer (String url);

    @Query("SELECT * FROM rssfeeds WHERE tag = :tag")
    List<RSSFeed> getRssAsTag (String tag);

}
