package com.imra.mynews.mvp.models;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Date: 26.04.2020
 * Time: 22:03
 *
 * @author IMRA027
 */

@Root(name = "enclosure", strict = false)
public class Enclosure implements Serializable {

    @Nullable
    @Attribute(name = "url")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
