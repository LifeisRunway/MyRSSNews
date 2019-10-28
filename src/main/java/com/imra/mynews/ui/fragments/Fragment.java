package com.imra.mynews.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import com.imra.mynews.R;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.presenters.RepositoryPresenter;
import com.imra.mynews.mvp.views.RepositoryView;
import com.imra.mynews.ui.views.RepositoryWidget;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Date: 27.07.2019
 * Time: 21:31
 *
 * @author IMRA027
 */
public class Fragment extends MvpAppCompatFragment implements RepositoryView {
    public static final String ARGS_REPOSITORY = "argsRepository";
    public static final String ARGS_POSITION = "argsPosition";

    @InjectPresenter
    RepositoryPresenter mFragmentPresenter;

    @BindView(R.id.textView)
    RepositoryWidget textView;

    @BindView(R.id.image_button_like)
    ImageButton mImageButton;

    private Unbinder unbinder;

    private Article mArticle;
    private int mPosition;

    @ProvidePresenter
    RepositoryPresenter provideFragmentPresenter() {
        mArticle = (Article) getArguments().get(ARGS_REPOSITORY);
        mPosition = (int) getArguments().get(ARGS_POSITION);
        return new RepositoryPresenter(mPosition,mArticle);
    }

    public static Fragment getInstance(int position, Article article) {
        Fragment fragment = new Fragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_REPOSITORY, article);
        args.putInt(ARGS_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_fragment, container, false);
//        if(getArguments() != null) {return null;}
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

//        mImageButton.setOnClickListener((View likeImageButton) -> {
//
//        });

        //mFragmentPresenter.setTitleText();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showRepository(int position, Article article) {
        mArticle = article;
        mPosition = position;
        textView.initWidget(getMvpDelegate(), article, position);
    }
}
