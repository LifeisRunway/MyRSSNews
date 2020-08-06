package com.imra.mynews.mvp.views;

import com.imra.mynews.mvp.models.Article;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndSingleStrategy;
import moxy.viewstate.strategy.StateStrategyType;

/**
 * Date: 28.07.2019
 * Time: 17:58
 *
 * @author IMRA027
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface RepositoryView extends MvpView {

    void showRepository(int position, Article article);

    void saveOrDelete(boolean isSave);

    void greenOrNot (boolean isSave);

}
