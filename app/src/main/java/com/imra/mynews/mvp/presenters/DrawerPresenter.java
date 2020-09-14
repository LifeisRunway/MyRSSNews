package com.imra.mynews.mvp.presenters;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.di.common.ArticleDao;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.views.DrawerView;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import moxy.InjectViewState;
import moxy.MvpPresenter;

/**
 * Date: 25.07.2020
 * Time: 19:53
 *
 * @author IMRA027
 */
@InjectViewState
public class DrawerPresenter extends MvpPresenter<DrawerView> {

    @Inject
    ArticleDao mAD;

//    @Inject
//    SharedPreferences mSP;
//
//    @Inject
//    SharedPreferences.Editor mSPEditor;

    Bundle bundle;
    List<String> urlRssFeeds;
    List<String> iconRssFeeds;
    List<String> tagRssFeeds;
    List<RSSFeed> mRssFeeds;
    RSSFeed tmpRss;
    FirebaseUser user;
    String tag;

    public String getIcon(String key) {
        return urlsAndIcons.get(key);
    }

    Map<String, String> urlsAndIcons;

    public DrawerPresenter (Bundle savedInstanceState) {
        MyNewsApp.getAppComponent().inject3(this);
        bundle = savedInstanceState;
        urlRssFeeds = new ArrayList<>();
        tagRssFeeds = new ArrayList<>();
        mRssFeeds = new ArrayList<>();
        urlsAndIcons = new HashMap<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        setTemp();
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().setDrawer(bundle);
    }

    public void addSubItem(String url, String iconUrl) {
        tmpRss = mAD.getRssForDrawer(url);
        getViewState().addSubItem(tmpRss);
    }

    public void setSubItems(Map<String, Object> firestoneData) {
        mRssFeeds.clear();
        tagRssFeeds.clear();
        if(!firestoneData.isEmpty()) {
            for(Map.Entry e : firestoneData.entrySet()) {
                if(mAD.getRssForDrawer(e.getKey().toString()) != null) {
                    mRssFeeds.add(mAD.getRssForDrawer(e.getKey().toString()));
                } else {
                    RSSFeed r = new RSSFeed();
                    r.setUrl(e.getKey().toString());
                    r.setIconUrl(e.getValue().toString());
                    String tag = e.getKey().toString()
                            .replaceFirst("[^/]+//(www\\.)*","")
                            .replaceFirst("/.+","");
                    r.setTag(tag);
                    //mAD.insertOrUpdateRss(r);
                    mRssFeeds.add(r);
                }
            }
        }

        if(!mRssFeeds.isEmpty()) {
            for(RSSFeed rssFeed : mRssFeeds) {
                if(!tagRssFeeds.contains(rssFeed.getTag())) {
                    tagRssFeeds.add(rssFeed.getTag());
                }
            }
        }
        getViewState().setSubItems(tagRssFeeds);
    }

    public List<String> getUrlRssFeeds() {
        return urlRssFeeds;
    }

    public void setTemp() {
        mRssFeeds = mAD.getAllRssFeeds();
        if(!mRssFeeds.isEmpty()) {
            for(RSSFeed rssFeed : mRssFeeds) {
                //if(rssFeed.getUrl() != null) {
                urlRssFeeds.add(rssFeed.getUrl());
                //}
            }
        }
    }

    public List<RSSFeed> getRssForTag (String tag) {
        return mAD.getRssAsTag(tag);
    }

    public void addNewNewsChannel(String name) {
        getViewState().addNewNewsChannel(name);
    }

    public boolean checkDouble (String nameChannel) {
        mRssFeeds.clear();
        mRssFeeds = mAD.getAllRssFeeds();
        if(!mRssFeeds.isEmpty()) {
            List<String> temp = new ArrayList<>();
            for (RSSFeed r : mRssFeeds) {

                if(r.getUrl() != null) {
                    temp.add(r.getUrl()
                            .replaceFirst("[^/]+//(www\\.)*","")
                            .replaceFirst("/.+",""));
                }
            }
            for(String s : temp) {
                if(s.equals(nameChannel)) return true;
            }
        }
        return false;
    }

    public String getIconUrl (String url) {
        return mAD.getRssForDrawer(url).getIconUrl();
    }

    public void deleteSubItem(String url) {
        mAD.deleteRssFeed(url);
    }

    public void deleteManySubItems(List<String> urls) {
        mAD.deleteManyRssFeeds(urls);
    }

    public FirebaseUser getUser () {
        return user;
    }


}
