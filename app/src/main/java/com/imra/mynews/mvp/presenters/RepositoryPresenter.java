package com.imra.mynews.mvp.presenters;

import androidx.annotation.NonNull;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.di.common.ArticleDao;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.views.RepositoryView;

import javax.inject.Inject;

import moxy.InjectViewState;
import moxy.MvpPresenter;

/**
 * Date: 28.07.2019
 * Time: 17:58
 *
 * @author IMRA027
 */

@InjectViewState
public class RepositoryPresenter extends MvpPresenter<RepositoryView> {

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

    public void clickSave () {
        if (mAD.getArticle(mArticle.getTitle()).isSaved()) {
            mArticle.setSaved(false);
            mAD.updateArticle(mArticle);
            getViewState().saveOrDelete(false);
            getViewState().greenOrNot(false);
        } else {
            mArticle.setSaved(true);
            mAD.updateArticle(mArticle);
            getViewState().saveOrDelete(true);
            getViewState().greenOrNot(true);
        }
    }

    public void greenOrNot () {
        if (mAD.getArticle(mArticle.getTitle()).isSaved()) {
            getViewState().greenOrNot(true);
        } else {
            getViewState().greenOrNot(false);
        }
    }


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showRepository(mPos, mArticle);
    }

}
