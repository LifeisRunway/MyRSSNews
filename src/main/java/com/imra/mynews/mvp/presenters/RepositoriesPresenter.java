package com.imra.mynews.mvp.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.mvp.MyNewsService;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.views.RepositoriesView;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date: 28.07.2019
 * Time: 14:40
 *
 * @author IMRA027
 */

@InjectViewState
public class RepositoriesPresenter extends BasePresenter<RepositoriesView>{

    @Inject
    MyNewsService myNewsService;

    private boolean mIsInLoading;

    public RepositoriesPresenter() {
        MyNewsApp.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach () {
        super.onFirstViewAttach();
        loadRepositories(false);
    }

    public void loadNextRepositories () {
        loadData(true, false);
    }

    public void loadRepositories (boolean isRefreshing) {
        loadData(false, isRefreshing);
    }

    private void loadData (boolean isPageLoading, boolean isRefreshing) {

        if (mIsInLoading) {
            return;
        }
        mIsInLoading = true;

        getViewState().onStartLoading();

        showProgress(isPageLoading, isRefreshing);

        final Observable<RSSFeed> observable = myNewsService.getRSSFeed();

        Subscription subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rssFeed -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingSuccess(isPageLoading, rssFeed);
                }, error -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingFailed(error);
                });
        unsubscribeOnDestroy(subscription);

    }

    private void onLoadingFinish(boolean isPageLoading, boolean isRefreshing) {
        mIsInLoading = false;

        getViewState().onFinishLoading();

        hideProgress(isPageLoading, isRefreshing);
    }

    private void onLoadingSuccess (boolean isPageLoading, RSSFeed rssFeeds) {

        if (isPageLoading) {
            getViewState().addRepositories(rssFeeds);
        } else {
            getViewState().setRepositories(rssFeeds);
        }
    }

    private void onLoadingFailed(Throwable error) {
        getViewState().showError(error.toString());
    }

    public void onErrorCancel() {
        getViewState().hideError();
    }

    private void showProgress(boolean isPageLoading, boolean isRefreshing) {
        if (isPageLoading) {
            return;
        }

        if (isRefreshing) {
            getViewState().showRefreshing();
        } else {
            getViewState().showListProgress();
        }
    }

    private void hideProgress(boolean isPageLoading, boolean isRefreshing) {
        if (isPageLoading) {
            return;
        }

        if (isRefreshing) {
            getViewState().hideRefreshing();
        } else {
            getViewState().hideListProgress();
        }
    }
}
