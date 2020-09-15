package com.imra.mynews.mvp.presenters;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.di.common.ArticleDao;
import com.imra.mynews.mvp.MyNewsService;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.ArticleDetail;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.models.RssFeedArticlesDetail;
import com.imra.mynews.mvp.views.RepositoriesView;

import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import moxy.InjectViewState;
import retrofit2.Response;

/**
 * Date: 28.07.2019
 * Time: 14:40
 *
 * @author IMRA027
 */

@Singleton
@InjectViewState
public class RepositoriesPresenter extends BasePresenter<RepositoriesView>{

    @Inject
    MyNewsService myNewsService;

    @Inject
    ArticleDao mAD;

    private boolean mIsInLoading;
    private boolean mIsInLoading2;
    private boolean mIsInLoading3;
    
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference docRefUserChannels;

    public RepositoriesPresenter() {
        MyNewsApp.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach () {
        super.onFirstViewAttach();
        //loadRepositories(false, "https://");
    }

    public void loadRepositories (boolean isRefreshing, String url, String iconUrl, boolean isConnected) {
        loadData(false, isRefreshing, url, iconUrl, isConnected);
    }

    public void findRSS (boolean isRefreshing, String url) {
        findRSSFeeds(false, isRefreshing, url);
    }

    public void offlineNews (boolean isRefreshing) {
        loadOfflineNews(false, isRefreshing);
    }

    public void forDrawer () {
        loadDataForDrawer();
    }


    private void loadData (boolean isPageLoading, boolean isRefreshing, String url, String iconUrl, boolean isConnected) {

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
                            saveRssToDB(rssFeed, url, iconUrl);
                            onLoadingSuccess(isPageLoading, getRssInDB(url));
                        }, error -> {
                            onLoadingFinish(isPageLoading, isRefreshing);
                            onLoadingFailed(error, url);
                        });
                unsubscribeOnDestroy(disposable);
            } else {
                RSSFeed r = new RSSFeed();
                r.setArticleList(new ArrayList<>());
                onLoadingFinish(isPageLoading, isRefreshing);
                onLoadingSuccess(isPageLoading, r);
            }
        } else {
            if(!url.equals("")) {

                Observable<RSSFeed> observable = Observable.just(getRssInDB(url));

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

    private RSSFeed getRssInDB (String url) {
        RSSFeed rssFeed;
        RssFeedArticlesDetail tempRFAD = mAD.getRssFeedArticleDetail2(url);
        if(tempRFAD != null) {
            rssFeed = tempRFAD.getRssFeed();
            List<Article> aList = checkAndClearArticlesInDB(tempRFAD.getArticles());
            rssFeed.setArticleList(aList);
        } else {
            rssFeed = new RSSFeed();
            rssFeed.setArticleList(new ArrayList<>());
        }
        return rssFeed;
    }
    
    private List<Article> checkAndClearArticlesInDB (List<Article> articles) {
        int maxArticles = 100;
        
        if(articles.size() <= maxArticles) {
            return smallToBig(articles);
        } 
        else {
            List<Article> saved = new ArrayList<>();
            List<Article> normals = new ArrayList<>();
        
            for (Article a : smallToBig(articles)) {
                (a.isSaved) ? saved.add(a) : normals.add(a);
            }
            mAD.deleteArticles(normals.subList(101, normals.size());
            normals = new ArrayList<>(normals.subList(0, normals.size() - saved.size());
            normals.addAll(saved);
            return smallToBig(normals);
        }       
    }

    @TargetApi(Build.VERSION_CODES.O)
    private List<Article> smallToBig (@NonNull List<Article> articles) {

        if(articles.get(0).getPubDate() != null) {
            String temp2 = articles.get(0).getPubDate();
            assert temp2 != null;
            DateTimeFormatter format2 = (temp2.substring(temp2.length()-3, temp2.length()).equals("GMT")) ?
                    DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss 'GMT'",  Locale.US).withZone(ZoneOffset.UTC) :
                    DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z", Locale.US).withZone(ZoneOffset.UTC);

            Collections.sort(articles, new Comparator<Article>() {
                @Override
                public int compare(Article o1, Article o2) {
                    TemporalAccessor date1 = format2.parse(o1.getPubDate());
                    Instant time = Instant.from(date1);
                    TemporalAccessor date2 = format2.parse(o2.getPubDate());
                    Instant time2 = Instant.from(date2);
                    return time2.compareTo(time);
                }
            });
        }
        return articles;
    }
    
    private String getTag (@NonNull String url) {
        return url
                .replaceFirst("[^/]+//(www\\.)*","")
                .replaceFirst("/.+","");    
    }

    private void saveRssToDB (RSSFeed rssFeed, String url, String iconUrl) {
            rssFeed.setUrl(url);
            if(!iconUrl.equals("")) {rssFeed.setIconUrl(iconUrl);}
            rssFeed.setTag(getTag(url));

            StringBuilder sb = new StringBuilder();
            mAD.insertRssFeed(rssFeed);
            RssFeedArticlesDetail temp = new RssFeedArticlesDetail();
            int tempRssID = mAD.getRssFeed(rssFeed.getUrl()).getRssFeedId();
            temp.setRssFeed(rssFeed);
            for(Article a : rssFeed.getArticleList()) {
                a.setRssId(tempRssID);
                if(a.getEnclosure() != null) {
                    a.setEclos(a.getEnclosure().getUrl());
                    a.setEnclosure(true);
                } else {
                    Pattern p2 = Pattern.compile("https*://[^\"']+\\.(png|jpg|jpeg|gif)");
                    if(a.getDescription() != null) {
                        Matcher m2 = p2.matcher(a.getDescription());
                        if(m2.find()) {
                            a.setEclos(m2.group());
                            a.setEnclosure(false);
                        }
                    }
                }
                sb.setLength(0);
                if(a.getCategoryList() != null && !a.getCategoryList().isEmpty()) {
                    for(String s : a.getCategoryList()) {
                        sb.append(s).append(", ");
                    }
                    a.setCategory(sb.substring(0, sb.length() - 2));
                }
            }
            temp.setArticles(rssFeed.getArticleList());
            mAD.insertOrUpdateRssFeedArticles(temp);
    }

    private void findRSSFeeds (boolean isPageLoading, boolean isRefreshing, String url) {

        if (mIsInLoading2) {
            return;
        }
        mIsInLoading2 = true;

        getViewState().onStartLoading();

        showProgress(isPageLoading, isRefreshing);

        if(!url.equals("")) {
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
        } else {
            onLoadingFinish(false, isRefreshing);
            onLoadingSuccess(false, findRssUrl("",url));
        }


    }

    private void loadOfflineNews (boolean isPageLoading, boolean isRefreshing) {
        if (mIsInLoading3) { return; }
        mIsInLoading3 = true;

        getViewState().onStartLoading();

        showProgress(isPageLoading, isRefreshing);

        RSSFeed tempRssFeed = new RSSFeed();
        if (mAD.getSavedArticles(true).isEmpty()) {
            tempRssFeed.setArticleList(new ArrayList<Article>());
        } else {
            tempRssFeed.setArticleList(smallToBig(mAD.getSavedArticles(true)));
        }

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


    private RSSFeed findRssUrl (String stringHtml, String url) {
        String sRssFeed = "<\\s*link\\s*(rel|type|title|href)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\"'<>\\s]+)\\s*(rel|type|title|href)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\"'<>\\s]+)\\s*(rel|type|title|href)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\"'<>\\s]+)\\s*(rel|type|title|href)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\"'<>\\s]+)\\s*/*>";
        String mRegex2 = "<\\s*link[^>]+(type\\s*=\\s*['\"]*image[^'\"]['\"]*[^>]+href\\s*=(\\s*['\"]*[^\"']+['\"]*)|href\\s*=(\\s*['\"]*[^\"']+['\"]*)[^>]+type\\s*=\\s*['\"]*image[^'\"]['\"]*)[^>]+";

        String smallIcon = "<\\s*link\\s*.+?(rel)\\s*=\\s*['\"]*([^'\"]*icon[^'\"]*)['\"]*\\s*.+?(href)\\s*=\\s*['\"]*([^'\"]+)['\"]*\\s*.+?\\/*>";

        Map<String, String> map = new HashMap<>();
        List<Article> articles = new ArrayList<>();
        RSSFeed rssFeeds = new RSSFeed();

        if(!stringHtml.equals("") && !url.equals("")) {
            Pattern pattern = Pattern.compile(sRssFeed);
            Matcher matcher = pattern.matcher(stringHtml);
            while (matcher.find()) {
                for (int i = 0; i < 7; i+=2) {
                    String name = matcher.group(i + 1).replace("\"","");
                    String value = matcher.group(i + 2).replace("\"","");
                    map.put(name, value);
                }

                if (map.get("rel").equals("alternate")) {
                    if(map.get("type").equals("application/atom+xml") || map.get("type").equals("application/rss+xml")){
                        Article article = new Article();
                        if(map.get("href").substring(0,1).equals("/")) {article.setDescription(url + map.get("href"));}
                        else {article.setDescription(map.get("href"));}
                        article.setTitle(map.get("title"));
                        articles.add(article);
                    }
                }
                map.clear();
            }

            if(!articles.isEmpty()) {
                pattern = Pattern.compile(smallIcon);
                matcher = pattern.matcher(stringHtml);
                if(matcher.find()) {

                    for (int i = 0; i < 3; i+=2) {
                        String name = matcher.group(i + 1);
                        String value = matcher.group(i + 2);
                        map.put(name, value);
                    }

                    if (map.get("rel").equals("apple-touch-icon") || map.get("rel").equals("icon") || map.get("rel").equals("shortcut icon")) {

                        String tmp = (map.get("href").substring(0,1).equals("/")) ?
                                url + map.get("href") :
                                map.get("href");

                        for(Article article : articles) {
                            article.setLink(tmp);
                        }
                    }
                }
            }
        }
        rssFeeds.setArticleList(articles);
        return rssFeeds;
    }


    private void loadDataForDrawer () {

            user = FirebaseAuth.getInstance().getCurrentUser();
            db = FirebaseFirestore.getInstance();
            Map<String, Object> userChannels = new HashMap<>();
            
            if(user != null) {
                docRefUserChannels = db.collection("userChannels").document(user.getEmail());
                docRefUserChannels
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if(task.getResult() != null && task.getResult().getData() != null) {
                               loadDataForDrawer2(task.getResult().getData());
                                //mDrawerPresenter.setSubItems(task.getResult().getData());
                                Log.e("getInFire task not null", task.getResult().getId() + " => " + task.getResult().getData());
                            } else {
                                //mDrawerPresenter.setSubItems(userChannels);
                            }
                        } else {
                            Log.w("ДОК_ОШИБКА", "Error getting documents.", task.getException());
                        }
                    });
            }
       
    }
    
    private void loadDataForDrawer2 (Map<String, Object> firestoneData) {
    
            if(!firestoneData.isEmpty()) {

                for(Map.Entry e : firestoneData.entrySet()) {
                    if(!e.getKey().equals("")) {
                        
                        if(mAD.getRssFeed(e.getKey().toString()) == null) {
                        
                           Observable<RSSFeed> observable = myNewsService.getRSSFeed(e.getKey().toString());

                            Disposable disposable = observable
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe((RSSFeed rssFeed) -> {
                                    saveRssToDB(rssFeed, e.getKey().toString(), e.getValue().toString());
                                }, error -> {
                                    onLoadingFailed(error, e.getKey().toString());
                                });
                            unsubscribeOnDestroy(disposable); 
                        
                        }

                        
                    }
                }
               
            }
    
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

//     private void onLoadingSuccess (boolean isPageLoading, List<ItemHtml> itemHtml) {

//         if (isPageLoading) {
//             getViewState().addRepositories(itemHtml);
//         } else {
//             getViewState().setRepositories(itemHtml);
//         }
//     }

    private void onLoadingFailed(Throwable error, String url) {
        String fixError = error.toString();
        String si = Log.getStackTraceString(error);
        System.out.println(si);

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
