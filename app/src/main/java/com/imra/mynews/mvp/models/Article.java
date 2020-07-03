package com.imra.mynews.mvp.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Date: 27.07.2019
 * Time: 20:03
 *
 * @author IMRA027
 */

@Entity(tableName = "articles")
@Root (name = "item", strict = false)
public class Article implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private long id;

    @Nullable
    @Element (name = "title")
    private String title;

    @Nullable
    @Element (name = "link")
    private String link;

    @Nullable
    @Element (name = "description", required = false)
    private String description;

    //@Nullable
    @Ignore
    @Element (name = "enclosure", required = false)
    private Enclosure enclosure;

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

    //@Nullable
    @Ignore
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
