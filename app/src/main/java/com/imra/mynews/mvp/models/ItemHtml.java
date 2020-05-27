package com.imra.mynews.mvp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Date: 25.05.2020
 * Time: 20:11
 *
 * @author IMRA027
 */

public class ItemHtml {
    @SerializedName("title")
    private String title;

    @SerializedName("type")
    private String type;

    @SerializedName("rel")
    private String rel;

    @SerializedName("href")
    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }
}
