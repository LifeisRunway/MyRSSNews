package com.imra.mynews.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.di.common.ArticleDao;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.views.RepositoryView;

import javax.inject.Inject;

/**
 * Date: 28.07.2019
 * Time: 17:58
 *
 * @author IMRA027
 */

@InjectViewState
public class RepositoryPresenter extends MvpPresenter <RepositoryView> {

    @Inject
    ArticleDao mAD;

    private Article mArticle;
    private int mPos;

    public RepositoryPresenter (int position, Article article) {
        super();
        MyNewsApp.getAppComponent().inject2(this);
        mArticle = article;
        mPos = position;
    }

    public void clickSave (Article article) {
        mAD.insert(article);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showRepository(mPos, mArticle);
    }

}
