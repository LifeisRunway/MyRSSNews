package com.imra.mynews.mvp.presenters;

import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.di.common.ArticleDao;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.views.DrawerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

    Bundle bundle;
    List<String> temp;
    List<RSSFeed> tempRssFeed;

    public DrawerPresenter (Bundle savedInstanceState) {
        MyNewsApp.getAppComponent().inject3(this);
        bundle = savedInstanceState;
        temp = new ArrayList<>();
        tempRssFeed = new ArrayList<>();
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().setDrawer(bundle);
    }

    public void setSubItems() {

        tempRssFeed = mAD.getAllRssFeeds();
        if(!tempRssFeed.isEmpty()) {
            for(RSSFeed rssFeed : tempRssFeed) {
                if(rssFeed.getUrl() != null) {
                    temp.add(rssFeed.getUrl());
                }
            }
            getViewState().setSubItems(temp);
        }
        temp.clear();
        tempRssFeed.clear();
    }

    public void addNewNewsChannel() {
        getViewState().addNewNewsChannel();
    }

    public void deleteSubItem(String url) {
        mAD.deleteRssFeed(url);
    }

//    private String changeUrl(String Tag) {
//
//    }

}
