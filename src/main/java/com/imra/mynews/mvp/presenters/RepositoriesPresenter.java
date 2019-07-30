package com.imra.mynews.mvp.presenters;

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

    public RepositoriesPresenter() {
        MyNewsApp.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach () {
        super.onFirstViewAttach();
        loadRepositories();
    }

    public void loadNextRepositories (int count) {
        int page = count/20 + 1;

    }

    public void loadRepositories () {
        loadData();
    }

    private void loadData () {

        final Observable<RSSFeed> observable = myNewsService.getRSSFeed();

        Subscription subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rssFeed -> {
                    onLoadingSuccess(rssFeed);
                }, error -> onLoadingFailed(error));
        unsubscribeOnDestroy(subscription);



    }

    private void onLoadingSuccess (RSSFeed rssFeeds) {
        getViewState().addRepositories(rssFeeds);
    }

    private void onLoadingFailed(Throwable error) {
        getViewState().showError(error.toString());
    }

    public void onErrorCancel() {
        getViewState().hideError();
    }
}
