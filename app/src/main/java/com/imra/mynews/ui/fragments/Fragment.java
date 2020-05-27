package com.imra.mynews.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.imra.mynews.R;

//import com.imra.mynews.di.modules.GlideApp;
import com.imra.mynews.di.modules.GlideApp;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.presenters.RepositoryPresenter;
import com.imra.mynews.mvp.views.RepositoryView;
import com.imra.mynews.ui.views.LinkWidget;
import com.imra.mynews.ui.views.RepositoryWidget;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

    @BindView(R.id.imageView2)
    ImageView mImageView;

    @BindView(R.id.textView)
    RepositoryWidget textView;

    @BindView(R.id.tvLink)
    LinkWidget tvLink;

    @BindView(R.id.image_button_like)
    ImageButton mImageButton;

//    @BindView(R.id.activity_home_frame_layout_details)
//    FrameLayout mDetailsFragmentLay;

    private Unbinder unbinder;

    private Activity mActivity;

    private Article mArticle;
    private int mPosition;

    private int duration = 500;
    private FrameLayout mFL;
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
        if(getActivity() != null) {
            mActivity = getActivity();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        if(mArticle.getEnclosure() != null) {
            GlideApp
                    .with(view)
                    .load(mArticle.getEnclosure().getUrl())
                    .fitCenter()
                    .into(mImageView);
        }

        mFL = (mActivity.findViewById(R.id.activity_home_frame_layout_details));

        mImageButton.setOnClickListener(v -> {
            YoYo.with(Techniques.SlideOutDown)
                    .duration(duration)
                    .playOn(mFL);

            Observable.just(mFL)
                    .subscribeOn(Schedulers.io())
                    .delay(duration, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mFL -> mFL.setVisibility(View.GONE))
                    .dispose();
        });
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
        tvLink.initWidget(getMvpDelegate(), article, position, mImageView);
    }

}
