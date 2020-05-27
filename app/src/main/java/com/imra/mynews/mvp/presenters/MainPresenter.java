package com.imra.mynews.mvp.presenters;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.views.MainInterface;

/**
 * Date: 27.07.2019
 * Time: 22:02
 *
 * @author IMRA027
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainInterface> {

    public void onRepositorySelection (int position, Article article) {
        getViewState().setSelection(position);

        getViewState().showDetails(position, article);

        getViewState().showDetailsContainer(position);
    }

    public void onRSSSelection (int position, RSSFeed rssFeed) {
        getViewState().setSelection(position);

        getViewState().showDetailsContainer(position);
    }

}
