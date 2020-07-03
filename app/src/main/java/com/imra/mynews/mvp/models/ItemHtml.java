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

    private String title;

    private String href;

    private String icon_url;

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

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
}
