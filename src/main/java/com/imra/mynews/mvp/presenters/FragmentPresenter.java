package com.imra.mynews.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.views.FragmentInterface;

/**
 * Date: 27.07.2019
 * Time: 21:33
 *
 * @author IMRA027
 */

@InjectViewState
public class FragmentPresenter extends MvpPresenter<FragmentInterface> {

    private Article mArticle;

    public FragmentPresenter (Article article) {
        super();
        mArticle = article;
    }


    public void setTitleText() {
        getViewState().setTitle();
    }

}
