package com.imra.mynews.ui.activities;

import android.animation.LayoutTransition;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.imra.mynews.R;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.presenters.MainPresenter;
import com.imra.mynews.mvp.presenters.RepositoriesPresenter;
import com.imra.mynews.mvp.views.MainInterface;
import com.imra.mynews.mvp.views.RepositoriesView;
import com.imra.mynews.ui.adapters.RepositoriesAdapter;
import com.imra.mynews.ui.fragments.Fragment;
import com.imra.mynews.ui.views.FrameSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainActivity extends MvpAppCompatActivity implements MainInterface, RepositoriesView, RepositoriesAdapter.OnScrollToBottomListener{

    @InjectPresenter
    MainPresenter mMainPresenter;

    @InjectPresenter
    RepositoriesPresenter mRepositoriesPresenter;

    @BindView(R.id.activity_home_swipe_refresh_layout)
    FrameSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.activity_home_progress_bar_repositories)
    ProgressBar mRepositoriesProgressBar;
    @BindView(R.id.activity_home_list_view_repositories)
    ListView mListView;
    @BindView(R.id.activity_home_text_view_no_repositories)
    TextView mNoRepositoriesTextView;

    @BindView(R.id.activity_home_frame_layout_details)
    FrameLayout mDetailsFragmeLayout;

//    @BindView(R.id.activity_home_toolbar)
//    Toolbar toolbar;

    private AlertDialog mErrorDialog;
    private Unbinder unbinder;

    private RepositoriesAdapter mReposAdapter;

    private int mCheck = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mDetailsFragmeLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }

        //setSupportActionBar(toolbar);

        mSwipeRefreshLayout.setListViewChild(mListView);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mRepositoriesPresenter.loadRepositories(true);
        });

        mReposAdapter = new RepositoriesAdapter(getMvpDelegate(), this);
        mListView.setAdapter(mReposAdapter);
        mListView.setOnItemClickListener((AdapterView<?> parent, View view, int pos, long id) -> {
            if(mReposAdapter.getItemViewType(pos) != 0) {
                return;
            }
            mMainPresenter.onRepositorySelection(pos, mReposAdapter.getItem(pos));
            //mMainPresenter.showInfo(view);
        });

    }

    @Override
    public void showDetailsContainer(int position) {

        if ((mDetailsFragmeLayout.getVisibility() == View.GONE)) {
            mDetailsFragmeLayout.setVisibility(View.VISIBLE);
            mCheck = position;
        } else if (mDetailsFragmeLayout.getVisibility() == View.VISIBLE){
            if ((position == mCheck)) {
                mDetailsFragmeLayout.setVisibility(View.GONE);
            } else {
                mCheck = position;
            }
        }

    }

    @Override
    public void setSelection(int position) {
        mReposAdapter.setSelection(position);
    }

    @Override
    public void showDetails(int position, Article article) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_home_frame_layout_details, Fragment.getInstance(position, article))
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onStartLoading() {
        mSwipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onFinishLoading() {
        mSwipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void showRefreshing() {
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
    }

    @Override
    public void hideRefreshing() {
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public void showListProgress() {
        mListView.setVisibility(View.GONE);
        mNoRepositoriesTextView.setVisibility(View.GONE);
        mRepositoriesProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideListProgress() {
        mListView.setVisibility(View.VISIBLE);
        mRepositoriesProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setRepositories(RSSFeed repositories) {
        mListView.setEmptyView(mNoRepositoriesTextView);
        mReposAdapter.setRepositories(repositories);
}

    @Override
    public void addRepositories(RSSFeed repositories) {
        mListView.setEmptyView(mNoRepositoriesTextView);
        mReposAdapter.addRepositories(repositories);
    }

    @Override
    public void showError(String message) {
        mErrorDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mRepositoriesPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError () {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.hide();
        }
    }

    @Override
    public void onScrollToBottom() {
        mRepositoriesPresenter.loadNextRepositories();
    }



}
