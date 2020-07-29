package com.imra.mynews.mvp.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Date: 27.07.2019
 * Time: 20:03
 *
 * @author IMRA027
 */

@Entity(tableName = "articles",
        foreignKeys = {@ForeignKey(entity = RSSFeed.class, parentColumns = "rssFeedId", childColumns = "rssId", onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "title", unique = true)})
@Root (name = "item", strict = false)
public class Article implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private Integer articleId;

    @ColumnInfo(name = "rssId")
    private Integer rssId;

    @ColumnInfo(name = "isSaved")
    private boolean isSaved;

    @Nullable
    @ColumnInfo(name = "title")
    @Element (name = "title")
    private String title;

    @Nullable
    @Element (name = "link")
    private String link;

    @Nullable
    @Element (name = "description", required = false)
    private String description;

    @Ignore
    @Element (name = "enclosure", required = false)
    private Enclosure enclosure;

    @Nullable
    private String eclos;

    @Nullable
    @Element (name = "pubDate", required = false)
    private String pubDate;

    @Nullable
    @Element (name = "dc:creator", required = false)
    private String creator;

    @Nullable
    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Nullable
    public Enclosure getEnclosure() {return enclosure;}

    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
    }

    @Nullable
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    @Nullable
    public String getEclos() {
        return eclos;
    }

    public void setEclos(@Nullable String eclos) {
        this.eclos = eclos;
    }

    public Integer getRssId() {
        return rssId;
    }

    public void setRssId(Integer rssId) {
        this.rssId = rssId;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }
}
