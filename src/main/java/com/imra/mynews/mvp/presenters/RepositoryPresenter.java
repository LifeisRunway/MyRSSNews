package com.imra.mynews.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.views.RepositoryView;

/**
 * Date: 28.07.2019
 * Time: 17:58
 *
 * @author IMRA027
 */

@InjectViewState
public class RepositoryPresenter extends MvpPresenter <RepositoryView> {

    private Article mArticle;

    public RepositoryPresenter (Article article) {
        super();

        mArticle = article;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showRepository(mArticle);
    }


}
