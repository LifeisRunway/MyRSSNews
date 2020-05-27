package com.imra.mynews.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.models.RSSFeed;

import java.util.List;

/**
 * Date: 28.07.2019
 * Time: 15:54
 *
 * @author IMRA027
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface RepositoriesView extends MvpView {

    void onStartLoading();

    void onFinishLoading();

    void showRefreshing();

    void hideRefreshing();

    void showListProgress();

    void hideListProgress();

    void showError(String message);

    void hideError();

    void setRepositories(RSSFeed repositories);
    void setRepositories(List<ItemHtml> itemHtml);

    void setChannelTitle(RSSFeed rssFeed);

    @StateStrategyType(AddToEndStrategy.class)
    void addRepositories(RSSFeed repositories);

    @StateStrategyType(AddToEndStrategy.class)
    void addRepositories(List<ItemHtml> itemHtml);

}
