package com.imra.mynews.mvp.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Date: 01.05.2020
 * Time: 12:29
 *
 * @author IMRA027
 */

@Root(name = "description", strict = false)
public class mDescription implements Serializable {

    @Attribute(name = "description")
    private String Description;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

}
