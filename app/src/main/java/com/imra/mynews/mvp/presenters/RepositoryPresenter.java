package com.imra.mynews.mvp.presenters;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.di.common.ArticleDao;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.models.RssFeedArticlesDetail;
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

    @Inject
    Integer mLocalDB;

    private Article mArticle;
    private int mPos;


    public RepositoryPresenter (int position, Article article) {
        super();
        MyNewsApp.getAppComponent().inject2(this);
        mArticle = article;
        mPos = position;
    }

    public void clickSave (@NonNull Article article) {
        //RssFeedArticlesDetail mRFAD = mAD.getRssFeedArticleDetail(mLocalDB);

//        if (mAD.getArticle(article.getTitle()) != null) {
//            mAD.deleteArticle(mLocalDB, article.getTitle());
//            getViewState().saveOrDelete(false);
//        } else {
//            article.setRssId(mLocalDB);
//            //mRFAD.setArticle(article);
//            mAD.saveArticles(article);
//            //mAD.insertRssFeedArticles(mRFAD);
//            getViewState().saveOrDelete(true);
//        }

        if (mAD.getArticle(article.getTitle()).isSaved()) {
            article.setSaved(false);
            mAD.updateArticle(article);
            getViewState().saveOrDelete(false);
            getViewState().greenOrNot(false);
        } else {
            article.setSaved(true);
            mAD.updateArticle(article);
            getViewState().saveOrDelete(true);
            getViewState().greenOrNot(true);
        }
    }

    public void greenOrNot (@NonNull Article article) {
        if (mAD.getArticle(article.getTitle()).isSaved()) {
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
