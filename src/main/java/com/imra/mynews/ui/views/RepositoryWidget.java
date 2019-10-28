package com.imra.mynews.ui.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

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

public class RepositoryWidget extends AppCompatTextView implements RepositoryView {
    private MvpDelegate mParentDelegate;
    private MvpDelegate mMvpDelegate;
    private Article mArticle;
    private int mPos;

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
        super(context, attrs, defStyleAttr);
    }

    @ProvidePresenter
    RepositoryPresenter provideRepositoryPresenter() {
        return new RepositoryPresenter(mPos, mArticle);
    }

    public void initWidget(MvpDelegate parentDelegate, Article article, int position) {
        mParentDelegate = parentDelegate;
        mArticle = article;
        mPos = position;
        getMvpDelegate().onCreate();
        getMvpDelegate().onAttach();
    }

    public MvpDelegate getMvpDelegate() {

        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
            mMvpDelegate.setParentDelegate(mParentDelegate, String.valueOf(mPos));
        }

        return mMvpDelegate;
    }

    @Override
    public void showRepository(int position, Article article) {
        setText(mArticle.getDescription());
    }

}
