package com.imra.mynews.mvp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
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
    private int rssFeedId;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "tag")
    private String tag;

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

    @Element(name = "url", required = false)
    @Path("channel/image")
    private String iconUrl;

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

    public int getRssFeedId() {
        return rssFeedId;
    }

    public void setRssFeedId(int rssFeedId) {
        this.rssFeedId = rssFeedId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
