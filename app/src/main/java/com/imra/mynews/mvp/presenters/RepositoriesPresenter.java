package com.imra.mynews.mvp.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.di.common.ArticleDao;
import com.imra.mynews.di.common.OfflineDB;
import com.imra.mynews.mvp.MyNewsService;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.models.RssFeedArticlesDetail;
import com.imra.mynews.mvp.views.RepositoriesView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Inject
    ArticleDao mAD;

    @Inject
    Integer mLocalDB;

    private boolean mIsInLoading;
    private boolean mIsInLoading2;
    private boolean mIsInLoading3;

    public RepositoriesPresenter() {
        MyNewsApp.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach () {
        super.onFirstViewAttach();
        //loadRepositories(false, "https://");
    }

    public void loadNextRepositories (String url, boolean isConnected) {
        loadData(true, false, url, isConnected);
    }

    public void loadRepositories (boolean isRefreshing, String url, boolean isConnected) {
        loadData(false, isRefreshing, url, isConnected);
    }

    public void findRSS (boolean isRefreshing, String url) {
        findRSSFeeds(false, isRefreshing, url);
    }

    public void offlineNews (boolean isRefreshing) {
        loadOfflineNews(false, isRefreshing);
    }

    private void loadData (boolean isPageLoading, boolean isRefreshing, String url, boolean isConnected) {

        if (mIsInLoading) {
            return;
        }
        mIsInLoading = true;

        getViewState().onStartLoading();

        showProgress(isPageLoading, isRefreshing);
        if(isConnected) {
            if(!url.equals("")) {
                Observable<RSSFeed> observable = myNewsService.getRSSFeed(url);

                Disposable disposable = observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((RSSFeed rssFeed) -> {
                            onLoadingFinish(isPageLoading, isRefreshing);
                            onLoadingSuccess(isPageLoading, rssFeed);
                            saveRssToDB(rssFeed);
                        }, error -> {
                            onLoadingFinish(isPageLoading, isRefreshing);
                            onLoadingFailed(error, url);
                        });
                unsubscribeOnDestroy(disposable);
            } else {
                onLoadingFinish(isPageLoading, isRefreshing);
                onLoadingSuccess(isPageLoading, new RSSFeed());
            }
        } else {
            if(!url.equals("")) {

                RSSFeed tempRssFeed;
                RssFeedArticlesDetail tempRFAD = mAD.getRssFeedArticleDetail2(url);
                tempRssFeed = tempRFAD.getRssFeed();
                tempRssFeed.setArticleList(tempRFAD.getArticles());


                Observable<RSSFeed> observable = Observable.just(tempRssFeed);

                Disposable disposable = observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((RSSFeed rssFeed) -> {
                            onLoadingFinish(isPageLoading, isRefreshing);
                            onLoadingSuccess(isPageLoading, rssFeed);
                        }, error -> {
                            onLoadingFinish(isPageLoading, isRefreshing);
                            onLoadingFailed(error, url);
                        });
                unsubscribeOnDestroy(disposable);
            }
        }


    }

    private void saveRssToDB (RSSFeed rssFeed) {

            RssFeedArticlesDetail temp = new RssFeedArticlesDetail();
            temp.setRssFeed(rssFeed);
            for(Article article : rssFeed.getArticleList()) {
                article.setRssId(rssFeed.getRssFeedId());
            }
            temp.setArticles(rssFeed.getArticleList());
            mAD.insertRssFeedArticles(temp);

    }

    private void findRSSFeeds (boolean isPageLoading, boolean isRefreshing, String url) {

        if (mIsInLoading2) {
            return;
        }
        mIsInLoading2 = true;

        getViewState().onStartLoading();

        showProgress(isPageLoading, isRefreshing);

        Observable<Response<String>> observables = myNewsService.findRSSFeeds(url);

        Disposable disposable = observables
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((stringResponse) -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingSuccess(isPageLoading, findRssUrl(stringResponse.body(),url));
                }, error -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingFailed(error, url);
                });
        unsubscribeOnDestroy(disposable);

    }

    private void loadOfflineNews (boolean isPageLoading, boolean isRefreshing) {
        if (mIsInLoading3) { return; }
        mIsInLoading3 = true;

        getViewState().onStartLoading();

        showProgress(isPageLoading, isRefreshing);

        RSSFeed tempRssFeed = new RSSFeed();
        RssFeedArticlesDetail tempRFAD = mAD.getRssFeedArticleDetail(mLocalDB);
        tempRssFeed.setChannelTitle(tempRFAD.getRssFeed().getChannelTitle());
        tempRssFeed.setArticleList(tempRFAD.getArticles());


        Observable<RSSFeed> obs = Observable.just(tempRssFeed);

        Disposable disposable = obs
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((rssFeed) -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingSuccess(isPageLoading, rssFeed);
                }, error -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingFailed(error, "");
                });
        unsubscribeOnDestroy(disposable);

    }


    private List<ItemHtml> findRssUrl (String stringHtml, String url) {
        String mRegex = "<\\s*link\\s*(rel|type|title|href)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\"'<>\\s]+)\\s*(rel|type|title|href)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\"'<>\\s]+)\\s*(rel|type|title|href)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\"'<>\\s]+)\\s*(rel|type|title|href)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\"'<>\\s]+)\\s*/*>";
        String mRegex2 = "<\\s*link[^>]+(type\\s*=\\s*['\"]*image[^'\"]['\"]*[^>]+href\\s*=(\\s*['\"]*[^\"']+['\"]*)|href\\s*=(\\s*['\"]*[^\"']+['\"]*)[^>]+type\\s*=\\s*['\"]*image[^'\"]['\"]*)[^>]+";
        Map<String, String> map = new HashMap<>();
        List<ItemHtml> itemHtmls = new ArrayList<>();
        Pattern pattern = Pattern.compile(mRegex);
        Matcher matcher = pattern.matcher(stringHtml);
        while (matcher.find()) {
            for (int i = 0; i < 7; i+=2) {
                String name = matcher.group(i + 1).replace("\"","");
                String value = matcher.group(i + 2).replace("\"","");
                map.put(name, value);
            }

            if (map.get("rel").equals("alternate")) {
                if(map.get("type").equals("application/atom+xml") || map.get("type").equals("application/rss+xml")){
                    ItemHtml itemHtml = new ItemHtml();
                    if(map.get("href").substring(0,1).equals("/")) itemHtml.setHref(url + map.get("href"));
                    else itemHtml.setHref(map.get("href"));
                    itemHtml.setTitle(map.get("title"));
                    itemHtmls.add(itemHtml);
                }
            }

            map.clear();
        }

        if(!itemHtmls.isEmpty()) {
            pattern = Pattern.compile(mRegex2);
            matcher = pattern.matcher(stringHtml);
            if(matcher.find()) {
                String group2 = "";
                String group3 = "";
                if(matcher.group(2) != null) {
                    group2 = matcher.group(2).replace("\"", "");
                    if(group2.substring(0, 1).equals("/")) {
                        group2 = url + group2;
                    }
                }
                if(matcher.group(3) != null) {
                    group3 = matcher.group(3).replace("\"", "");
                    if(group3.substring(0, 1).equals("/")) {
                        group3 = url + group3;
                    }
                }

                for(ItemHtml itemHtml : itemHtmls) {
                    if(!group2.equals("")) itemHtml.setIcon_url(group2);
                    if(!group3.equals("")) itemHtml.setIcon_url(group3);
                }
            }
        }

        return new ArrayList<>(itemHtmls);
    }

    private void onLoadingFinish(boolean isPageLoading, boolean isRefreshing) {
        mIsInLoading = false;
        mIsInLoading2 = false;
        mIsInLoading3 = false;

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

    private void onLoadingFailed(Throwable error, String url) {
        String fixError = error.toString();
        //String si = Log.getStackTraceString(error);
        //System.out.println(si);

        if(error.getClass() == UnknownHostException.class) {
            fixError = "Невозможно подключиться к:\n\"" + url + "\"\nПроверьте правильность адреса и доступ к интернету";
        }
        getViewState().showError(fixError);
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
