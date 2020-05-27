package com.imra.mynews.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.mvp.MyNewsService;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.views.RepositoriesView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    private boolean mIsInLoading2;
    //private String APP_PREFERENCES_URL = "https://habr.com/rss";

    public RepositoriesPresenter() {
        MyNewsApp.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach () {
        super.onFirstViewAttach();
        //loadRepositories(false, "https://");
    }

    public void loadNextRepositories (String url) {
        loadData(true, false, url);
    }

    public void loadRepositories (boolean isRefreshing, String url) {
        loadData(false, isRefreshing, url);
    }

    public void findRSS (boolean isRefreshing, String url) {
        findRSSFeeds(false, isRefreshing, url);
    }

    private void loadData (boolean isPageLoading, boolean isRefreshing, String url) {

        if (mIsInLoading) {
            return;
        }
        mIsInLoading = true;

        getViewState().onStartLoading();

        showProgress(isPageLoading, isRefreshing);

        Observable<RSSFeed> observable = myNewsService.getRSSFeed(url);

        Disposable disposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((RSSFeed rssFeed) -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingSuccess(isPageLoading, rssFeed);
                }, error -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingFailed(error);
                });
        unsubscribeOnDestroy(disposable);

    }

    private void findRSSFeeds (boolean isPageLoading, boolean isRefreshing, String url) {

        if (mIsInLoading2) {
            return;
        }
        mIsInLoading2 = true;

        getViewState().onStartLoading();

        showProgress(isPageLoading, isRefreshing);

        Observable<List<ItemHtml>> observables = myNewsService.findRSSFeeds(url);

        Disposable disposable = observables
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((List<ItemHtml> html) -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingSuccess(isPageLoading, html);
                }, error -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingFailed(error);
                });
        unsubscribeOnDestroy(disposable);

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
            setChannelTitle(rssFeeds);
        }
    }

    private void onLoadingSuccess (boolean isPageLoading, List<ItemHtml> itemHtml) {

        if (isPageLoading) {
            getViewState().addRepositories(itemHtml);
        } else {
            getViewState().setRepositories(itemHtml);
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

    private void setChannelTitle (RSSFeed rssFeed) {
        getViewState().setChannelTitle(rssFeed);
    }


}
