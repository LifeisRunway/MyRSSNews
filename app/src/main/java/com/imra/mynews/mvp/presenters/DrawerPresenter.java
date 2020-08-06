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
public
class DrawerPresenter extends MvpPresenter<DrawerView> {

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
    List<RSSFeed> mRssFeeds;
    FirebaseUser user;

    Map<String, String> urlsAndIcons;

    public DrawerPresenter (Bundle savedInstanceState) {
        MyNewsApp.getAppComponent().inject3(this);
        bundle = savedInstanceState;
        urlRssFeeds = new ArrayList<>();
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

    public void addSubItem(String url) {
        urlsAndIcons.clear();
        RSSFeed r = mAD.getRssForDrawer(url);
        if(r != null) {
            urlsAndIcons.put(r.getUrl(), r.getIconUrl());
        }
        getViewState().addSubItem(urlsAndIcons);
    }

    public void setSubItems() {
        //urlRssFeeds.clear();
        //iconRssFeeds.clear();
        mRssFeeds.clear();

        urlsAndIcons.clear();
        mRssFeeds = mAD.getAllRssFeeds();
        if(!mRssFeeds.isEmpty()) {
            for(RSSFeed rssFeed : mRssFeeds) {

                    //urlRssFeeds.add(rssFeed.getUrl());
                    //iconRssFeeds.add(rssFeed.getIconUrl());
                    urlsAndIcons.put(rssFeed.getUrl(),rssFeed.getIconUrl());

            }
        }
        getViewState().setSubItems(urlsAndIcons);
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

    public void addNewNewsChannel() {
        getViewState().addNewNewsChannel();
    }

    public void deleteSubItem(String url) {
        mAD.deleteRssFeed(url);
    }

    public FirebaseUser getUser () {
        return user;
    }


}
