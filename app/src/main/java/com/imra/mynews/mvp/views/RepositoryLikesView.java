package com.imra.mynews.mvp.views;

import java.util.List;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndSingleStrategy;
import moxy.viewstate.strategy.StateStrategyType;

/**
 * Date: 01.02.2020
 * Time: 19:04
 *
 * @author IMRA027
 */
public interface RepositoryLikesView extends MvpView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void updateLikes(List<Integer> inProgress, List<Integer> likedIds);
}