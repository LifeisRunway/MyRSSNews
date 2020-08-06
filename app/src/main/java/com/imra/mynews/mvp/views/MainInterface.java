package com.imra.mynews.mvp.views;

import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.ItemHtml;
import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndSingleStrategy;
import moxy.viewstate.strategy.OneExecutionStateStrategy;
import moxy.viewstate.strategy.StateStrategyType;

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
