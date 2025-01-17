package com.imra.mynews.mvp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

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
    private int articleId;

    @ColumnInfo(name = "rssId", index = true)
    private int rssId;

    @ColumnInfo(name = "isSaved")
    private boolean isSaved;

    @Nullable
    @ColumnInfo(name = "title")
    @Element (name = "title", required = false)
    private String title;

    @Nullable
    @Element (name = "link", required = false)
    private String link;

    @Nullable
    @Element (name = "description", required = false)
    private String description;

    @Ignore
    @Element (name = "enclosure", required = false)
    private Enclosure enclosure;

    @Nullable
    private String eclos;

    public boolean isEnclosure() {
        return isEnclosure;
    }

    public void setEnclosure(boolean enclosure) {
        isEnclosure = enclosure;
    }

    @ColumnInfo(name = "isEnclosure")
    private boolean isEnclosure;

    @Nullable
    @Element (name = "pubDate", required = false)
    private String pubDate;

    @Nullable
    @ColumnInfo(name = "creator")
    @Element(name = "creator", required = false)
    private String creator;

    @Nullable
    @ColumnInfo(name = "category")
    private String category;

    @Ignore
    @ElementList(entry = "category", required = false, data = true, inline = true)
    private List<String> categoryList;

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

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    @Nullable
    public String getEclos() {
        return eclos;
    }

    public void setEclos(@Nullable String eclos) {
        this.eclos = eclos;
    }

    public int getRssId() {
        return rssId;
    }

    public void setRssId(int rssId) {
        this.rssId = rssId;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    @Nullable
    public String getCategory() {
        return category;
    }

    public void setCategory(@Nullable String category) {
        this.category = category;
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
    }

}
