package com.imra.mynews.ui.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.presenters.RepositoryPresenter;
import com.imra.mynews.mvp.views.RepositoryView;

/**
 * Date: 29.07.2019
 * Time: 22:52
 *
 * @author IMRA027
 */

public class RepositoryWidget extends TextView implements RepositoryView {
    private MvpDelegate mParentDelegate;
    private MvpDelegate mMvpDelegate;
    private Article mArticle;

    @InjectPresenter
    RepositoryPresenter mRepositoryPresenter;

    public RepositoryWidget(Context context) {
        super(context);
    }

    public RepositoryWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RepositoryWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RepositoryWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @ProvidePresenter
    RepositoryPresenter provideRepositoryPresenter() {
        return new RepositoryPresenter(mArticle);
    }

    public void initWidget(MvpDelegate parentDelegate, Article article) {
        mParentDelegate = parentDelegate;
        mArticle = article;

        getMvpDelegate().onCreate();
        getMvpDelegate().onAttach();
    }

    public MvpDelegate getMvpDelegate() {
        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
            mMvpDelegate.setParentDelegate(mParentDelegate, String.valueOf(1));
        }
        return mMvpDelegate;
    }

    @Override
    public void showRepository(Article article) {
        setText(article.getTitle());
    }

}
