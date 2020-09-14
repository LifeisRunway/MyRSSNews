package com.imra.mynews.mvp.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/**
 * Date: 13.09.2020
 * Time: 20:16
 *
 * @author IMRA027
 */
public class ArticleDetail {

    public ArticleDetail  () {}

    @Embedded
    private Article article;

    @Relation(parentColumn = "articleId", entityColumn = "artId")
    private Enclosure enclosure;

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Enclosure getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
    }


}
