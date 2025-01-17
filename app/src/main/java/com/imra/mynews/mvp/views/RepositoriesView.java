package com.imra.mynews.mvp.views;

import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.models.RSSFeed;

import java.util.List;
import java.util.Map;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndSingleStrategy;
import moxy.viewstate.strategy.AddToEndStrategy;
import moxy.viewstate.strategy.OneExecutionStateStrategy;
import moxy.viewstate.strategy.StateStrategyType;

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

    void setChannelTitle(RSSFeed rssFeed);

    @StateStrategyType(AddToEndStrategy.class)
    void addRepositories(RSSFeed repositories);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void setFirestoneMap(Map<String, Object> firestoneMap);

}
