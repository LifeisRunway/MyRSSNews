package com.imra.mynews.mvp.views;

import android.view.View;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.models.RSSFeed;

/**
 * Date: 27.07.2019
 * Time: 22:01
 *
 * @author IMRA027
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface MainInterface extends MvpView {

    void setSelection(int position);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showDetailsContainer(int position);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showDetailsContainer(int position, ItemHtml itemHtml);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showDetails(int position, Article article);

}
