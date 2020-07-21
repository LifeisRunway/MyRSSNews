package com.imra.mynews.mvp.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Date: 27.07.2019
 * Time: 20:03
 *
 * @author IMRA027
 */
@Entity(tableName = "rssfeeds", indices = {@Index(value = "title", unique = true)})
@Root(name = "rss", strict = false)
public class RSSFeed {

    @PrimaryKey(autoGenerate = true)
    private Integer rssFeedId;

    @ColumnInfo(name = "url")
    private String url;

    @Nullable
    @ColumnInfo(name = "title")
    @Element(name="title")
    @Path("channel")
    private String channelTitle;

    @Element(name="description", required = false)
    @Path("channel")
    private String channelDescription;

    @Ignore
    @ElementList(name="item", inline=true)
    @Path("channel")
    private List<Article> articleList;

    @Nullable
    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(@Nullable String channelTitle) {
        this.channelTitle = channelTitle;
    }

    @Ignore
    public List<Article> getArticleList() {
        return articleList;
    }

    @Ignore
    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }

    public Integer getRssFeedId() {
        return rssFeedId;
    }

    public void setRssFeedId(Integer rssFeedId) {
        this.rssFeedId = rssFeedId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
