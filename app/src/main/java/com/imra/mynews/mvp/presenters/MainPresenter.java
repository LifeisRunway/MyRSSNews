package com.imra.mynews.mvp.presenters;


import android.content.SharedPreferences;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.views.MainInterface;

import javax.inject.Inject;

import moxy.InjectViewState;
import moxy.MvpPresenter;

/**
 * Date: 27.07.2019
 * Time: 22:02
 *
 * @author IMRA027
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainInterface> {

    @Inject
    SharedPreferences mSP;

    @Inject
    SharedPreferences.Editor mSPEditor;

    static final String MY_URL = "url";

    public MainPresenter() {
        MyNewsApp.getAppComponent().inject4(this);
    }


    public void onRepositorySelection (int position, Article article) {
        getViewState().setSelection(position);

        getViewState().showDetails(position, article);

        getViewState().showDetailsContainer(position);
    }

    public void onRSSSelection (int position, ItemHtml itemHtml) {
        getViewState().setSelection(position);

        getViewState().showDetailsContainer(position, itemHtml);
    }

    public SharedPreferences getSP() {
        return mSP;
    }

    public void clearSP (String url) {
        mSPEditor.remove(url).apply();
    }

    public SharedPreferences.Editor getEditor() {
        return mSPEditor;
    }

    public void saveSP (String data) {
        mSPEditor.putString("url", data).apply();
    }

    public boolean isUrl (String url) {
        return !mSP.getString(MY_URL, "").equals(url);
    }
}
