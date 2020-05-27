package com.imra.mynews.mvp.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
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

    @Attribute(name = "url")
    private String url;

    @Attribute(name = "length")
    private String length;

    @Attribute(name = "type")
    private String type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
