package com.imra.mynews.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

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