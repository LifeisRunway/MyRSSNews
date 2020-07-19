package com.imra.mynews.mvp.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 14.07.2020
 * Time: 22:26
 *
 * @author IMRA027
 */
public class RssFeedArticlesDetail {

    public RssFeedArticlesDetail () {}

    @Embedded
    private RSSFeed rssFeed;

    @Relation(parentColumn = "rssFeedId", entityColumn = "rssId")
    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public RSSFeed getRssFeed() {
        return rssFeed;
    }

    public void setRssFeed(RSSFeed rssFeed) {
        this.rssFeed = rssFeed;
    }
}
