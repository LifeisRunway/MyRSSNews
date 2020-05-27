package com.imra.mynews.mvp.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Date: 27.07.2019
 * Time: 20:03
 *
 * @author IMRA027
 */

@Root (name = "item", strict = false)
public class Article implements Serializable{

    @Element (name = "title")
    private String title;

    @Element (name = "link")
    private String link;

    @Element (name = "description", required = false)
    private String description;

    @Element (name = "enclosure", required = false)
    private Enclosure enclosure;

    @Element (name = "pubDate")
    private String pubDate;

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Enclosure getEnclosure() {return enclosure;}

    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
    }

}
