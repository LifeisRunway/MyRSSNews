package com.imra.mynews.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;

/**
 * Date: 28.07.2019
 * Time: 17:58
 *
 * @author IMRA027
 */
public interface RepositoryView extends MvpView {

    void showRepository(int position, Article article);

    void saveOrDelete(boolean isSave);

    void greenOrNot (boolean isSave);

}
